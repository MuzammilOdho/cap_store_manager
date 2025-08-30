package com.noor.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.math.BigDecimal;

public class SupplierDTO {
    public record Request(
            @NotBlank @Size(min = 1, max = 120) String name,
            String phoneNumber,
            String address
    ) {}

    public record Response(Long id, String name, String phoneNumber, String address, Instant createdAt) {}
}
