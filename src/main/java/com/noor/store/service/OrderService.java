package com.noor.store.service;

import com.noor.store.dto.OrderDTO;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.OrderMapper;
import com.noor.store.model.*;
import com.noor.store.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final BuyerRepository buyerRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementService stockMovementService;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Create an order (purchase or sale). Calculates totals from items.
     * For PURCHASE: creates inbound stock movements.
     * For SALE: consumes FIFO, sets cogs on items and creates sale movement entry.
     */
    public OrderDTO.OrderResponse create(OrderDTO.OrderRequest req) {
        // validate
        OrderType type = OrderType.valueOf(req.orderType);
        if (type == OrderType.PURCHASE && req.supplierId == null)
            throw new IllegalArgumentException("supplierId required for purchase");
        if (type == OrderType.SALE && req.buyerId == null)
            throw new IllegalArgumentException("buyerId required for sale");

        Order o = new Order();
        o.setOrderType(type);
        o.setOrderDate(req.orderDate != null ? req.orderDate : java.time.LocalDate.now());
        o.setDueDate(req.dueDate != null ? req.dueDate : o.getOrderDate().plusDays(30));
        o.setNotes(req.notes);

        if (req.supplierId != null) {
            o.setSupplier(supplierRepository.findById(req.supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + req.supplierId)));
        }
        if (req.buyerId != null) {
            o.setBuyer(buyerRepository.findById(req.buyerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + req.buyerId)));
        }

        // Build items
        List<OrderItem> items = req.items.stream().map(it -> {
            Category cat = categoryRepository.findById(it.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + it.categoryId()));
            OrderItem oi = new OrderItem();
            oi.setCategory(cat);
            oi.setQuantity(it.quantity());
            oi.setUnitPrice(it.unitPrice());
            oi.setLineTotal(it.unitPrice().multiply(BigDecimal.valueOf(it.quantity())));
            oi.setOrder(o);
            return oi;
        }).collect(Collectors.toList());
        o.setItems(items);

        // Order number
        o.setOrderNumber(generateOrderNumber(type));
        // totals (from items)
        BigDecimal total = items.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        o.setTotalAmount(total);
        o.setPaidAmount(BigDecimal.ZERO);
        o.setRemainingAmount(total);
        o.setStatus(OrderStatus.UNPAID);

        // Save order with cascade items
        Order saved = orderRepository.save(o);

        // Stock movements
        if (saved.getOrderType() == OrderType.PURCHASE) {
            for (OrderItem item : saved.getItems()) {
                stockMovementService.createMovement(new com.noor.store.dto.StockMovementDTO.Request(
                        item.getCategory().getId(),
                        MovementType.PURCHASE.name(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice(),
                        java.time.LocalDate.now(),
                        item.getId(),
                        false,
                        "Purchase from order " + saved.getOrderNumber()
                ));
            }
        } else {
            // SALE: consume FIFO and set COGS per item
            for (OrderItem item : saved.getItems()) {
                BigDecimal cogs = stockMovementService.consumeStockFIFO(item.getCategory().getId(), item.getQuantity());
                item.setCostOfGoodsSold(cogs);
                item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                orderItemRepository.save(item);

                // record sale movement but skip stock decrement (we already decreased stock in FIFO)
                BigDecimal unitCostForMovement = item.getQuantity() > 0 ? cogs.divide(BigDecimal.valueOf(item.getQuantity()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
                stockMovementService.createMovement(new com.noor.store.dto.StockMovementDTO.Request(
                        item.getCategory().getId(),
                        MovementType.SALE.name(),
                        item.getQuantity(),
                        unitCostForMovement,
                        item.getUnitPrice(),
                        java.time.LocalDate.now(),
                        item.getId(),
                        true,
                        "Sale from order " + saved.getOrderNumber()
                ));
            }
        }

        // Recalculate totals and status (payments possibly exist)
        recalcOrderTotals(saved);

        return orderMapper.toResponse(orderRepository.save(saved));
    }

    @Transactional(readOnly = true)
    public OrderDTO.OrderResponse get(Long id) {
        return orderMapper.toResponse(orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    /**
     * List with pageable. Use findFiltered for filtering - default fetches all non-deleted.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> list(Pageable pageable) {
        return orderRepository.findFiltered(null, null, null, null, null, null, pageable).map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> filter(OrderType type, java.time.LocalDate start, java.time.LocalDate end, OrderStatus status, Long buyerId, Long supplierId, Pageable pageable) {
        return orderRepository.findFiltered(type, start, end, status, buyerId, supplierId, pageable).map(orderMapper::toResponse);
    }

    public void softDelete(Long id) {
        Order o = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (!o.getPayments().isEmpty()) throw new IllegalStateException("Cannot delete order with payments");
        o.setDeleted(true);
        orderRepository.save(o);
    }

    private String generateOrderNumber(OrderType type) {
        Optional<Order> last = orderRepository.findTopByOrderByIdDesc();
        long next = last.map(o -> o.getId() + 1).orElse(1L);
        String prefix = type == OrderType.SALE ? "SO" : "PO";
        return String.format("%s-%06d", prefix, next);
    }

    /**
     * Recalculate totals from items and payments; set status accordingly.
     */
    public void recalcOrderTotals(Order order) {
        if (order == null) return;
        // ensure items loaded
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        BigDecimal paid = paymentRepository.findAllByOrderId(order.getId()).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPaidAmount(paid);
        order.setRemainingAmount(total.subtract(paid));

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0)
            order.setStatus(OrderStatus.FULLY_PAID);
        else if (order.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)
            order.setStatus(OrderStatus.PARTIALLY_PAID);
        else
            order.setStatus(OrderStatus.UNPAID);
    }
}
