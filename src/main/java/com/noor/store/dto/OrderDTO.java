package com.noor.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderDTO {

    public record ItemRequest(
            @NotNull Long categoryId,
            @NotNull @Min(1) Integer quantity,
            @NotNull BigDecimal unitPrice
    ) {}

    public record ItemResponse(Long id, Long categoryId, String categoryName, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal, BigDecimal cogs) {}

    public static class OrderRequest {
        @NotNull
        public String orderType; // PURCHASE or SALE

        public Long supplierId; // required for PURCHASE
        public Long buyerId;    // required for SALE

        public LocalDate orderDate;
        public LocalDate dueDate;

        public String notes;

        @NotNull
        @Size(min = 1)
        public List<ItemRequest> items;
    }

    public record OrderResponse(Long id, String orderNumber, String orderType, Long supplierId, String supplierName,
                                Long buyerId, String buyerName, LocalDate orderDate, LocalDate dueDate,
                                BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal remainingAmount,
                                String status, String notes, List<ItemResponse> items) {}
}
