package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

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
                payment.getGuid(),
                payment.getInquiryRefId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTransactionRefId(),
                payment.getStatus(),
                payment.getNote(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
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

    public Page<PaymentDto> convert(Page<Payment> payments) {
        List<PaymentDto> dtos = payments.get()
                                        .map(this::convert)
                                        .toList();
        return new PageImpl<>(dtos, payments.getPageable(), payments.getTotalElements());
    }
}
