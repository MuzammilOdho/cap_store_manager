package com.noor.store.dto;

import com.noor.store.model.PaymentMethod;
import com.noor.store.model.PaymentType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentDTO {

    public record Request(
            @NotNull Long orderId,
            @NotNull BigDecimal amount,
            LocalDate paymentDate,
            PaymentMethod paymentMethod,
            PaymentType paymentType,
            String notes
    ) {}

    public record Response(Long id, Long orderId, String orderNumber, BigDecimal amount, LocalDate paymentDate, PaymentMethod paymentMethod, PaymentType paymentType, String notes) {}
}
