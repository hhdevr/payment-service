package com.iprody.paymentserviceapp.rest;

import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;
import com.iprody.paymentserviceapp.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentConverter paymentConverter;

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService, paymentConverter);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /payments should return list of two PaymentDto")
    void findAll_ReturnsListOfTwoPayments() throws Exception {
        // given
        Instant now = Instant.now();

        Payment p1 = new Payment();
        p1.setId(1L);
        p1.setAmount(BigDecimal.valueOf(100.50));
        p1.setDescription("First");
        p1.setCreatedAt(now);
        p1.setUpdatedAt(now);

        Payment p2 = new Payment();
        p2.setId(2L);
        p2.setAmount(BigDecimal.valueOf(200.75));
        p2.setDescription("Second");
        p2.setCreatedAt(now);
        p2.setUpdatedAt(now);

        PaymentDto d1 = new PaymentDto(
                1L,
                BigDecimal.valueOf(100.50),
                "First",
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(now, ZoneOffset.UTC)
        );

        PaymentDto d2 = new PaymentDto(
                2L,
                BigDecimal.valueOf(200.75),
                "Second",
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(now, ZoneOffset.UTC)
        );

        when(paymentService.findAll()).thenReturn(List.of(p1, p2));
        when(paymentConverter.convert(List.of(p1, p2))).thenReturn(List.of(d1, d2));

        // when / then
        mockMvc.perform(get("/payments").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].amount").value(100.50))
               .andExpect(jsonPath("$[0].description").value("First"))
               .andExpect(jsonPath("$[1].id").value(2))
               .andExpect(jsonPath("$[1].amount").value(200.75))
               .andExpect(jsonPath("$[1].description").value("Second"));
    }

    @Test
    @DisplayName("GET /payments should return empty list when no payments")
    void findAll_ReturnsEmptyList() throws Exception {
        // given
        when(paymentService.findAll()).thenReturn(List.of());
        when(paymentConverter.convert(List.of())).thenReturn(List.of());

        // when / then
        mockMvc.perform(get("/payments")
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /payments/{id} should return PaymentDto when found")
    void getById_ReturnsPayment_WhenFound() throws Exception {
        // given
        Long id = 10L;
        Instant now = Instant.now();

        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmount(BigDecimal.TEN);
        payment.setDescription("Test payment");
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        PaymentDto dto = new PaymentDto(
                id,
                BigDecimal.TEN,
                "Test payment",
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(now, ZoneOffset.UTC)
        );

        when(paymentService.findById(id)).thenReturn(Optional.of(payment));
        when(paymentConverter.convert(payment)).thenReturn(dto);

        // when / then
        mockMvc.perform(get("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(id))
               .andExpect(jsonPath("$.amount").value(10))
               .andExpect(jsonPath("$.description").value("Test payment"));
    }

    @Test
    @DisplayName("GET /payments/{id} should return 404 when payment not found")
    void getById_ReturnsNotFound_WhenMissing() throws Exception {
        // given
        Long id = 42L;
        when(paymentService.findById(id)).thenReturn(Optional.empty());

        // when / then
        mockMvc.perform(get("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}
