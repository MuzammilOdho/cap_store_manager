package com.noor.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class StockAdjustmentDTO {
    public record Request(
            @NotNull Long categoryId,
            @NotNull String adjustmentType,
            @NotNull @Min(1) Integer quantityChanged,
            String reason
    ) {}

    public record Response(Long id, Long categoryId, String categoryName, String adjustmentType, Integer quantityChanged, String reason, LocalDateTime adjustmentDate) {}
}
