package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.exception.ServiceException;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.QPaymentFilter;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.paymentserviceapp.exception.ErrorMessage.PAYMENT_NOT_EXIST;

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
                                           .orElseThrow(() -> new ServiceException(PAYMENT_NOT_EXIST, id)));

    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        return converter.convert(repository.save(converter.convert(dto)));
    }

    @Override
    public PaymentDto update(PaymentDto dto) {
        if (!repository.existsById(dto.guid())) {
            throw new ServiceException(PAYMENT_NOT_EXIST, dto.guid());
        }
        return converter.convert(repository.save(converter.convert(dto)));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ServiceException(PAYMENT_NOT_EXIST, id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean updateNote(UUID id, String note) {
        if (repository.updateNote(id, note) == 0) {
            throw new ServiceException(PAYMENT_NOT_EXIST, id);
        }
        return true;
    }

    public List<PaymentDto> search(PaymentFilter filter) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return converter.convert(repository.findAll(spec));
    }

    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        Page<Payment> page = repository.findAll(spec, pageable);
        return page.map(converter::convert);
    }

    public Page<PaymentDto> searchQPaged(QPaymentFilter filter) {
        Page<Payment> page = repository.findAll(filter.createPredicate(),
                                                filter.createPageable());
        return page.map(converter::convert);
    }
}
