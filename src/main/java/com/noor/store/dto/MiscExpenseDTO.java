package com.noor.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MiscExpenseDTO {
    public record Request(@NotBlank String description, @NotNull BigDecimal amount, LocalDate expenseDate, String notes) {}
    public record Response(Long id, String description, BigDecimal amount, LocalDate expenseDate, String notes) {}
}
