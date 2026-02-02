package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.async.AsyncSender;
import com.iprody.paymentserviceapp.async.XPaymentAdapterRequestMessage;
import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.converter.XPaymentAdapterMapper;
import com.iprody.paymentserviceapp.exception.ServiceException;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.QPaymentFilter;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.paymentserviceapp.exception.ErrorMessage.PAYMENT_NOT_EXIST;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentConverter converter;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

    @Autowired
    public PaymentServiceImpl(PaymentRepository repository,
                              PaymentConverter converter,
                              XPaymentAdapterMapper xPaymentAdapterMapper,
                              AsyncSender<XPaymentAdapterRequestMessage> sender) {
        this.repository = repository;
        this.converter = converter;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
        this.sender = sender;
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
                                           .orElseThrow(() -> {
                                               log.error("Paymnent not found with id {}", id);
                                               return new ServiceException(PAYMENT_NOT_EXIST, id);
                                           }));

    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = converter.convert(dto);
        Payment saved = repository.save(entity);
        PaymentDto resultDto = converter.convert(saved);

        XPaymentAdapterRequestMessage requestMessage = xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(entity);
        sender.send(requestMessage);

        return resultDto;
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
            log.error("Unexisted Paymnent with id {} could not be deleted", id);
            throw new ServiceException(PAYMENT_NOT_EXIST, id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean updateNote(UUID id, String note) {
        if (repository.updateNote(id, note) == 0) {
            log.error("Unexisted Paymnent with id {} could not be updated", id);
            throw new ServiceException(PAYMENT_NOT_EXIST, id);
        }
        return true;
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        Payment payment = repository.findById(id)
                                    .orElseThrow(() -> new ServiceException(PAYMENT_NOT_EXIST, id));
        payment.setStatus(status);
        payment.setUpdatedAt(OffsetDateTime.now());
        return converter.convert(repository.save(payment));
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
