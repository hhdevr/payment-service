package com.iprody.paymentserviceapp.converter;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
class PaymentConverterImplTest {

    private PaymentConverter converter = new PaymentConverterImpl();

    private Payment samplePayment;
    private OffsetDateTime testDateTime = OffsetDateTime.of(2025,
                                                            11,
                                                            26,
                                                            11,
                                                            4,
                                                            0,
                                                            0,
                                                            ZoneOffset.UTC);

    private final UUID guid1 = UUID.randomUUID();
    private final UUID guid2 = UUID.randomUUID();
    private final UUID inquiryRefId1 = UUID.randomUUID();
    private final UUID inquiryRefId2 = UUID.randomUUID();
    private final UUID transactionRefId1 = UUID.randomUUID();

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
        assertThat(result.guid()).isEqualTo(guid1);
        assertThat(result.inquiryRefId()).isEqualTo(inquiryRefId1);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100.50));
        assertThat(result.currency()).isEqualTo("USD");
        assertThat(result.transactionRefId()).isEqualTo(transactionRefId1);
        assertThat(result.status()).isEqualTo(PaymentStatus.DECLINED);
        assertThat(result.note()).isEqualTo("Test payment note");
        assertThat(result.createdAt()).isEqualTo(testDateTime);
        assertThat(result.updatedAt()).isEqualTo(testDateTime);
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
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should convert multiple Payments to PaymentDtos")
    void convert_MultiplePayments_ReturnsPaymentDtoList() {
        // given
        Payment payment2 = new Payment();
        payment2.setGuid(guid2);
        payment2.setInquiryRefId(inquiryRefId2);
        payment2.setAmount(BigDecimal.valueOf(200.75));
        payment2.setCurrency("KRW");
        payment2.setTransactionRefId(null);
        payment2.setStatus(PaymentStatus.PENDING);
        payment2.setNote("Second payment note");
        payment2.setCreatedAt(testDateTime.plusHours(1));
        payment2.setUpdatedAt(testDateTime.plusHours(2));

        List<Payment> payments = Arrays.asList(samplePayment, payment2);

        // when
        List<PaymentDto> result = converter.convert(payments);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).guid()).isEqualTo(guid1);
        assertThat(result.get(1).guid()).isEqualTo(guid2);
        assertThat(result.get(0).inquiryRefId()).isEqualTo(inquiryRefId1);
        assertThat(result.get(1).inquiryRefId()).isEqualTo(inquiryRefId2);
        assertThat(result.get(0).currency()).isEqualTo("USD");
        assertThat(result.get(1).currency()).isEqualTo("KRW");
        assertThat(result.get(0).status()).isEqualTo(PaymentStatus.DECLINED);
        assertThat(result.get(1).status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.get(0).note()).isEqualTo("Test payment note");
        assertThat(result.get(1).note()).isEqualTo("Second payment note");
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
        payment.setGuid(guid1);
        payment.setInquiryRefId(inquiryRefId1);
        payment.setAmount(BigDecimal.valueOf(100.50));
        payment.setCurrency("USD");
        payment.setTransactionRefId(transactionRefId1);
        payment.setStatus(PaymentStatus.DECLINED);
        payment.setNote("Test payment note");
        payment.setCreatedAt(testDateTime);
        payment.setUpdatedAt(testDateTime);
        return payment;
    }
}
