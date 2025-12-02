package com.iprody.paymentserviceapp.controller.model;

import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentDto(
        UUID guid,
        UUID inquiryRefId,
        BigDecimal amount,
        String currency,
        UUID transactionRefId,
        PaymentStatus status,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
