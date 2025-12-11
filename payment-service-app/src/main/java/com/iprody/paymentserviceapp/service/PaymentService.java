package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(UUID id);

    PaymentDto getById(UUID id);

    List<PaymentDto> findByStatus(PaymentStatus status);

    List<PaymentDto> search(PaymentFilter filter);

    Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable);
}
