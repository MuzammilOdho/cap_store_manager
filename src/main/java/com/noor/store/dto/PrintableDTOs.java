package com.noor.store.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PrintableDTOs {
    public record PrintableOrder(Long id, String orderNumber, String orderType, LocalDate orderDate, String buyerName, String supplierName, List<PrintableOrderItem> items, BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal remainingAmount, String status, String notes) {}
    public record PrintableOrderItem(Long id, String categoryName, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal, BigDecimal cogs) {}
    public record PrintablePayment(Long id, Long orderId, String orderNumber, String buyerName, String supplierName, BigDecimal amount, BigDecimal orderTotal, BigDecimal orderPaid, BigDecimal orderRemaining, LocalDate paymentDate, String paymentMethod, String paymentType, String notes) {}
}
