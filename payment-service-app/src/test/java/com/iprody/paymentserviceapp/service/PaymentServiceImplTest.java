package com.iprody.paymentserviceapp.service;

import com.chaykin.paymentserviceapi.AsyncSender;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterRequestMessage;
import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.converter.PaymentConverterImpl;
import com.iprody.paymentserviceapp.converter.XPaymentAdapterMapper;
import com.iprody.paymentserviceapp.exception.ServiceException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Instancio.create;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @Mock
    private XPaymentAdapterMapper xPaymentAdapterMapper;

    @Mock
    private AsyncSender<XPaymentAdapterRequestMessage> sender;

    @BeforeEach
    void setUp() {
        PaymentConverter paymentConverter = new PaymentConverterImpl();
        paymentService = new PaymentServiceImpl(paymentRepository,
                                                paymentConverter,
                                                xPaymentAdapterMapper,
                                                sender);
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

    @Test
    @DisplayName("create() should save and return new PaymentDto")
    void create_SavesAndReturnsPaymentDto() {
        // given
        PaymentDto dto = create(PaymentDto.class);
        Payment entity = new PaymentConverterImpl().convert(dto);

        when(paymentRepository.save(entity)).thenReturn(entity);

        // when
        PaymentDto result = paymentService.create(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.guid()).isEqualTo(dto.guid());
        verify(paymentRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("update() should update existing PaymentDto")
    void update_UpdatesExistingPayment() {
        // given
        PaymentDto dto = create(PaymentDto.class);
        Payment entity = new PaymentConverterImpl().convert(dto);

        when(paymentRepository.existsById(dto.guid())).thenReturn(true);
        when(paymentRepository.save(entity)).thenReturn(entity);

        // when
        PaymentDto result = paymentService.update(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.guid()).isEqualTo(dto.guid());
        verify(paymentRepository, times(1)).existsById(dto.guid());
        verify(paymentRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("update() should throw ServiceException when payment not exists")
    void update_ThrowsException_WhenNotExists() {
        // given
        PaymentDto dto = create(PaymentDto.class);
        when(paymentRepository.existsById(dto.guid())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.update(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Payment id=" + dto.guid() + " does not exist");
        verify(paymentRepository, times(1)).existsById(dto.guid());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete existing payment")
    void delete_DeletesExistingPayment() {
        // given
        UUID id = UUID.randomUUID();
        when(paymentRepository.existsById(id)).thenReturn(true);

        // when
        paymentService.delete(id);

        // then
        verify(paymentRepository, times(1)).existsById(id);
        verify(paymentRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("delete() should throw ServiceException when payment not exists")
    void delete_ThrowsException_WhenNotExists() {
        // given
        UUID id = UUID.randomUUID();
        when(paymentRepository.existsById(id)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.delete(id))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Payment id=" + id + " does not exist");
        verify(paymentRepository, times(1)).existsById(id);
        verify(paymentRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("updateNote() should return true when note updated successfully")
    void updateNote_ReturnsTrue_WhenUpdated() {
        // given
        UUID id = UUID.randomUUID();
        String note = "new note";
//        when(paymentRepository.existsById(id)).thenReturn(true);
        when(paymentRepository.updateNote(id, note)).thenReturn(1);

        // when
        boolean result = paymentService.updateNote(id, note);

        // then
        assertThat(result).isTrue();
        verify(paymentRepository, times(1)).updateNote(id, note);
    }

    @Test
    @DisplayName("updateNote() should throw ServiceException when payment not exists")
    void updateNote_ThrowsException_WhenNotExists() {
        // given
        UUID id = UUID.randomUUID();
        String note = "new note";
        when(paymentRepository.updateNote(id, note)).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> paymentService.updateNote(id, note))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Payment id=" + id + " does not exist");
        verify(paymentRepository, times(1)).updateNote(id, note);
    }

}
