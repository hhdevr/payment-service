package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(UUID id);

    List<PaymentDto> findByStatus(PaymentStatus status);

}
