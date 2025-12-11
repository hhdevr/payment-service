package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.converter.PaymentConverterImpl;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.create;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        PaymentConverter paymentConverter = new PaymentConverterImpl();
        paymentService = new PaymentServiceImpl(paymentRepository, paymentConverter);
    }

    @Test
    @DisplayName("findAll() should return a list of PaymentDto")
    void findAll_ReturnsListOfPaymentDto() {
        // given
        List<Payment> payments = Instancio.ofList(Payment.class)
                                          .size(3)
                                          .create();

        when(paymentRepository.findAll()).thenReturn(payments);

        // when
        List<PaymentDto> result = paymentService.findAll();

        // then
        assertThat(result).hasSize(3);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() should return an empty list when the repository is empty")
    void findAll_ReturnsEmptyList_WhenRepositoryEmpty() {
        // given
        when(paymentRepository.findAll()).thenReturn(emptyList());

        // when
        List<PaymentDto> result = paymentService.findAll();

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() should return an Optional with PaymentDto when found")
    void findById_ReturnsPaymentDto_WhenFound() {
        // given
        Payment payment = create(Payment.class);
        UUID id = payment.getGuid();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        // when
        Optional<PaymentDto> result = paymentService.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().guid()).isEqualTo(id);
        assertThat(result.get().amount()).isEqualTo(payment.getAmount());
        assertThat(result.get().note()).isEqualTo(payment.getNote());
        assertThat(result.get().createdAt()).isNotNull();
        assertThat(result.get().updatedAt()).isNotNull();
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById() should return Optional.empty() when not found")
    void findById_ReturnsEmpty_WhenNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<PaymentDto> result = paymentService.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findById(id);
    }
}
