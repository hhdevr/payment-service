package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;

import java.util.List;

public interface PaymentConverter {

    PaymentDto convert(Payment payment);

    List<PaymentDto> convert(List<Payment> payments);

}
