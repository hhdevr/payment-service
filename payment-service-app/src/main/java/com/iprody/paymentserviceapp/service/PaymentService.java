package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(UUID id);

    PaymentDto getById(UUID id);

    List<PaymentDto> findByStatus(PaymentStatus status);

}
