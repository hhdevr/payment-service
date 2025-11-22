package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistence.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<Payment> findAll();

    Optional<Payment> findById(Long id);

}
