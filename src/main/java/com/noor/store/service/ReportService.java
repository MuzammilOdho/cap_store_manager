package com.noor.store.service;

import com.noor.store.dto.ReportDTO;
import com.noor.store.model.*;
import com.noor.store.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final MiscExpenseRepository miscExpenseRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    public BigDecimal totalSales(LocalDate start, LocalDate end) {
        BigDecimal v = orderRepository.sumTotalAmountByTypeBetween(OrderType.SALE, start, end);
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal totalPurchases(LocalDate start, LocalDate end) {
        BigDecimal v = orderRepository.sumTotalAmountByTypeBetween(OrderType.PURCHASE, start, end);
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal actualReceived(LocalDate start, LocalDate end) {
        BigDecimal v = paymentRepository.sumPaymentsReceivedBetween(start, end);
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal actualSpent(LocalDate start, LocalDate end) {
        BigDecimal v = paymentRepository.sumPaymentsToSuppliersBetween(start, end);
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal totalExpenses(LocalDate start, LocalDate end) {
        BigDecimal v = miscExpenseRepository.sumByDateRange(start, end);
        return v == null ? BigDecimal.ZERO : v;
    }

    public List<ReportDTO.CategoryReport> categoryReport(LocalDate start, LocalDate end) {
        List<com.noor.store.model.Category> cats = categoryRepository.findAll();
        return cats.stream().map(c -> {
            List<OrderItem> items = orderRepository.findOrderItemsByCategory(c.getId(), start, end);
            BigDecimal totalSold = items.stream().map(it -> it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new ReportDTO.CategoryReport(c.getId(), c.getName(), c.getCurrentStock(), totalSold);
        }).collect(Collectors.toList());
    }

    public ReportDTO.Dashboard dashboard(LocalDate start, LocalDate end) {
        List<Order> recentOrders = orderRepository.findAll(PageRequest.of(0,5, org.springframework.data.domain.Sort.by("orderDate").descending())).getContent();
        List<com.noor.store.model.Payment> recentPayments = paymentRepository.findAll(PageRequest.of(0,5, org.springframework.data.domain.Sort.by("paymentDate").descending())).getContent();
        var ro = recentOrders.stream().map(o -> new ReportDTO.RecentOrder(o.getId(), o.getOrderNumber(), o.getOrderType().name(), o.getTotalAmount(), o.getPaidAmount(), o.getRemainingAmount(), o.getStatus().name(), o.getOrderDate())).toList();
        var rp = recentPayments.stream().map(p -> new ReportDTO.RecentPayment(p.getId(), p.getOrder().getOrderNumber(), p.getAmount(), p.getPaymentDate())).toList();

        BigDecimal sales = totalSales(start, end);
        BigDecimal purchases = totalPurchases(start, end);
        BigDecimal receiv = defaultZero(orderRepository.totalReceivables());
        BigDecimal payab = defaultZero(orderRepository.totalPayables());
        BigDecimal expenses = totalExpenses(start, end);

        // Accrual profit: revenue - COGS - expenses
        BigDecimal cogs = defaultZero(orderItemRepository.sumCogsForSalesBetween(start, end));
        BigDecimal profit = sales.subtract(cogs).subtract(expenses);
        return new ReportDTO.Dashboard(sales, purchases, receiv, payab, expenses, profit, ro, rp);
    }

    private BigDecimal defaultZero(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
