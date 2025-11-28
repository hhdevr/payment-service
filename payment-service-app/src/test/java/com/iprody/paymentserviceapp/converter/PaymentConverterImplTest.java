package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
class PaymentConverterImplTest {

    private PaymentConverter converter = new PaymentConverterImpl();

    private Payment samplePayment;
    private Instant testInstant = Instant.parse("2025-11-26T11:04:00Z");
    private LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(testInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        samplePayment = getTestPayment();
    }

    @Test
    @DisplayName("should convert single Payment to PaymentDto correctly")
    void convert_SinglePayment_ReturnsPaymentDto() {
        // when
        PaymentDto result = converter.convert(samplePayment);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100.50));
        assertThat(result.description()).isEqualTo("Test payment");
        assertThat(result.createdAt()).isEqualTo(expectedLocalDateTime);
        assertThat(result.updatedAt()).isEqualTo(expectedLocalDateTime);
    }

    @Test
    @DisplayName("should return null when Payment is null")
    void convert_NullPayment_ReturnsNull() {
        // when
        PaymentDto result = converter.convert((Payment) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should convert empty list to empty list")
    void convert_EmptyList_ReturnsEmptyList() {
        // when
        List<PaymentDto> result = converter.convert(emptyList());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should convert null list to empty list")
    void convert_NullList_ReturnsEmptyList() {
        // when
        List<PaymentDto> result = converter.convert((List<Payment>) null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should convert multiple Payments to PaymentDtos")
    void convert_MultiplePayments_ReturnsPaymentDtoList() {
        // given
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setAmount(BigDecimal.valueOf(200.75));
        payment2.setDescription("Second payment");
        payment2.setCreatedAt(testInstant.plusSeconds(3600));
        payment2.setUpdatedAt(testInstant.plusSeconds(7200));

        List<Payment> payments = Arrays.asList(samplePayment, payment2);

        // when
        List<PaymentDto> result = converter.convert(payments);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(0).amount()).isEqualTo(BigDecimal.valueOf(100.5));
        assertThat(result.get(1).amount()).isEqualTo(BigDecimal.valueOf(200.75));
        assertThat(result.get(0).description()).isEqualTo("Test payment");
        assertThat(result.get(1).description()).isEqualTo("Second payment");
        assertThat(result.get(0).createdAt()).isBefore(result.get(1).createdAt());
        assertThat(result.get(0).updatedAt()).isBefore(result.get(1).updatedAt());
    }

    @Test
    @DisplayName("should handle null Instant fields gracefully")
    void convert_PaymentWithNullTimestamps_HandlesNulls() {
        // given
        samplePayment.setCreatedAt(null);
        samplePayment.setUpdatedAt(null);

        // when & then
        assertThatNoException().isThrownBy(() -> converter.convert(samplePayment));
    }

    private Payment getTestPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(BigDecimal.valueOf(100.50));
        payment.setDescription("Test payment");
        payment.setCreatedAt(testInstant);
        payment.setUpdatedAt(testInstant);
        return payment;
    }
}
