package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class PaymentConverterImpl implements PaymentConverter {

    @Override
    public PaymentDto convert(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentDto(
                payment.getId(),
                payment.getAmount(),
                payment.getDescription(),
                toLocalDateTime(payment.getCreatedAt()),
                toLocalDateTime(payment.getUpdatedAt())
        );
    }

    @Override
    public List<PaymentDto> convert(List<Payment> payments) {
        if (isEmpty(payments)) {
            return emptyList();
        }

        return payments.stream()
                       .map(this::convert)
                       .toList();
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
