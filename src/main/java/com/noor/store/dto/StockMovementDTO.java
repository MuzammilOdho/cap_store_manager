package com.noor.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockMovementDTO {

    public record Request(
            @NotNull Long categoryId,
            @NotNull String movementType, // PURCHASE, SALE, MANUAL_ADD, MANUAL_REMOVE
            @NotNull @Min(1) Integer quantity,
            java.math.BigDecimal unitCost,
            java.math.BigDecimal unitPrice,
            LocalDate movementDate,
            Long orderItemId,
            Boolean skipStockDecrement,
            String notes
    ) {}

    public record Response(Long id, Long categoryId, String categoryName, Integer quantity, Integer remainingQuantity,
                           BigDecimal unitCost, BigDecimal unitPrice, String movementType, LocalDate movementDate, Long orderItemId) {}
}
