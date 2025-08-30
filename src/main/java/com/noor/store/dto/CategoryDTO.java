package com.noor.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class CategoryDTO {

    public record Request(
            @NotBlank @Size(min = 1, max = 120) String name,
            String description
    ) {}

    public record Response(Long id, String name, String description, Integer currentStock, Instant createdAt) {}
}
