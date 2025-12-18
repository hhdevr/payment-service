package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentConverter {

    PaymentDto convert(Payment payment);

    Payment convert(PaymentDto payment);

    List<PaymentDto> convert(List<Payment> payments);
}
