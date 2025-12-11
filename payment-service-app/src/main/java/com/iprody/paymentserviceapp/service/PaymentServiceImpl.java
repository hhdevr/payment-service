package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.QPaymentFilter;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        return repository.findAll()
                         .stream()
                         .map(converter::convert)
                         .toList();
    }

    @Override
    public Optional<PaymentDto> findById(UUID id) {
        return repository.findById(id)
                         .map(converter::convert);
    }

    @Override
    public PaymentDto getById(UUID id) {
        return converter.convert(repository.findById(id)
                                           .orElseThrow(() -> new EntityNotFoundException(
                                                   "Payment not found with id " + id)));

    }

    @Override
    public List<PaymentDto> findByStatus(PaymentStatus status) {
        return repository.findByStatus(status).stream()
                         .map(converter::convert)
                         .toList();
    }

    public List<PaymentDto> search(PaymentFilter filter) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return converter.convert(repository.findAll(spec));
    }

    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return converter.convert(repository.findAll(spec, pageable));
    }

    public Page<PaymentDto> searchQPaged(QPaymentFilter filter) {
        return converter.convert(repository.findAll(filter.createPredicate(),
                                                    filter.createPageable()));
    }
}
