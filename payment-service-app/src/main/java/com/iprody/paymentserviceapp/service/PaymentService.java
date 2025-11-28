package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.rest.model.PaymentDto;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(Long id);

}
