package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.QPaymentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> findAll();

    Optional<PaymentDto> findById(UUID id);

    PaymentDto getById(UUID id);

    PaymentDto create(PaymentDto dto);

    PaymentDto update(PaymentDto dto);

    void delete(UUID id);

    boolean updateNote(UUID id, String note);

    List<PaymentDto> search(PaymentFilter filter);

    Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable);

    Page<PaymentDto> searchQPaged(QPaymentFilter filter);
}
