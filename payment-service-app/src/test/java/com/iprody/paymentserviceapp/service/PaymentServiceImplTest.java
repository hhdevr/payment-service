package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.instancio.Instancio.createList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("findAll() should return a list of payments")
    void findAll_ReturnsListOfPayments() {
        // given
        List<Payment> payments = createList(Payment.class);

        when(paymentRepository.findAll()).thenReturn(payments);

        // when
        List<Payment> result = paymentService.findAll();

        // then
        assertThat(result).hasSize(payments.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() should return an empty list when the repository is empty")
    void findAll_ReturnsEmptyList_WhenRepositoryEmpty() {
        // given
        when(paymentRepository.findAll()).thenReturn(emptyList());

        // when
        List<Payment> result = paymentService.findAll();

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() should return an Optional with Payment when found")
    void findById_ReturnsPayment_WhenFound() {
        // given
        Payment payment = create(Payment.class);
        Long id = payment.getId();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        // when
        Optional<Payment> result = paymentService.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById() should return Optional.empty() when not found")
    void findById_ReturnsEmpty_WhenNotFound() {
        // given
        Long id = 42L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Payment> result = paymentService.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findById(id);
    }
}
