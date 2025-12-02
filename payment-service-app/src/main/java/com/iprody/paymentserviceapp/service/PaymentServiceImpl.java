package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentConverter converter;

    @Autowired
    public PaymentServiceImpl(PaymentRepository repository,
                              PaymentConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public List<PaymentDto> findAll() {
        return repository.findAll().stream()
                         .map(converter::convert)
                         .toList();
    }

    @Override
    public Optional<PaymentDto> findById(UUID id) {
        return repository.findById(id)
                         .map(converter::convert);
    }

    @Override
    public List<PaymentDto> findByStatus(PaymentStatus status) {
        return repository.findByStatus(status).stream()
                         .map(converter::convert)
                         .toList();
    }
}

