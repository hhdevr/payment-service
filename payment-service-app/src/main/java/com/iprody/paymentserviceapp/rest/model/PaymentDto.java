package com.iprody.paymentserviceapp.rest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
        Long id,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}