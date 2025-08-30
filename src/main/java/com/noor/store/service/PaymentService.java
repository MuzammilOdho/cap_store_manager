package com.noor.store.service;

import com.noor.store.dto.PaymentDTO;
import com.noor.store.exception.BusinessException;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.PaymentMapper;
import com.noor.store.model.*;
import com.noor.store.repository.OrderRepository;
import com.noor.store.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;

    public PaymentDTO.Response create(PaymentDTO.Request req) {
        Order order = orderRepository.findById(req.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + req.orderId()));

        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("Amount must be > 0");

        // allow small rounding tolerance; but disallow payments that exceed total by > 0.01
        BigDecimal newPaid = order.getPaidAmount().add(req.amount());
        if (newPaid.subtract(order.getTotalAmount()).compareTo(new BigDecimal("0.01")) > 0)
            throw new BusinessException("Payment exceeds order total");

        Payment p = paymentMapper.toEntity(req);
        p.setOrder(order);
        p.setPaymentDate(req.paymentDate() != null ? req.paymentDate() : LocalDate.now());
        p.setPaymentType(order.getOrderType() == OrderType.SALE ? PaymentType.SALE_PAYMENT : PaymentType.PURCHASE_PAYMENT);

        Payment saved = paymentRepository.save(p);

        // Recalculate totals
        orderService.recalcOrderTotals(order);
        orderRepository.save(order);

        return paymentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentDTO.Response get(Long id) {
        return paymentMapper.toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO.Response> list(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::toResponse);
    }

    public void delete(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));

        Order order = p.getOrder();
        paymentRepository.delete(p);

        // Recalculate totals after deletion
        orderService.recalcOrderTotals(order);
        orderRepository.save(order);
    }

    public Page<PaymentDTO.Response> listByOrder(Long orderId, Pageable pageable) {
        return paymentRepository.findByOrderId(orderId, pageable).map(paymentMapper::toResponse);
    }

    public Page<PaymentDTO.Response> recentPayments(int limit) {
        return paymentRepository.findRecent(PageRequest.of(0, Math.max(1, limit))).map(paymentMapper::toResponse);
    }
}
