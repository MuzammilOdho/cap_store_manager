package com.noor.store.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportDTO {
    public record CategoryReport(Long categoryId, String categoryName, Integer currentStock, BigDecimal totalSold) {}
    public record RecentOrder(Long id, String orderNumber, String orderType, BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal remainingAmount, String status, LocalDate orderDate) {}
    public record RecentPayment(Long id, String orderNumber, BigDecimal amount, LocalDate paymentDate) {}
    public record Dashboard(BigDecimal totalSales, BigDecimal totalPurchases, BigDecimal totalReceivables, BigDecimal totalPayables, BigDecimal totalExpenses, BigDecimal profit, List<RecentOrder> recentOrders, List<RecentPayment> recentPayments) {}
}
