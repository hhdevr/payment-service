package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentConverter {

    PaymentDto convert(Payment payment);

    List<PaymentDto> convert(List<Payment> payments);

    Page<PaymentDto> convert(Page<Payment> payments);

}
