package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.iprody.paymentserviceapp.persistence.model.PaymentStatus.DECLINED;
import static org.instancio.Select.field;
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

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Test
    @DisplayName("GET /payments should return list of two PaymentDto")
    void findAll_ReturnsListOfTwoPayments() throws Exception {
        // given
        PaymentDto d1 = Instancio.create(PaymentDto.class);

        PaymentDto d2 = Instancio.create(PaymentDto.class);

        when(paymentService.findAll()).thenReturn(List.of(d1, d2));

        // when & then
        mockMvc.perform(get("/payments").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].guid").value(d1.guid().toString().toLowerCase()));
    }

    @Test
    @DisplayName("GET /payments should return empty list when no payments")
    void findAll_ReturnsEmptyList() throws Exception {
        // given
        when(paymentService.findAll()).thenReturn(List.of());

        // when & then
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
        PaymentDto dto = Instancio.create(PaymentDto.class);

        when(paymentService.getById(dto.guid())).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/payments/{id}", dto.guid())
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.guid").value(dto.guid().toString().toLowerCase()));
    }

    @Test
    @DisplayName("GET /payments/{id} should return 404 when payment not found")
    void getById_ReturnsNotFound_WhenMissing() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        when(paymentService.getById(id))
                .thenThrow(new EntityNotFoundException("Payment not found with id " + id));

        // when & then
        mockMvc.perform(get("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /payments/statuses?status=COMPLETED,PENDING returns payments with those statuses")
    void getByStatus_ReturnsPaymentsByStatus() throws Exception {
        // given
        PaymentDto d1 = Instancio.of(PaymentDto.class)
                                 .set(field(PaymentDto::status), PaymentStatus.APPROVED)
                                 .create();
        PaymentDto d2 = Instancio.of(PaymentDto.class)
                                 .set(field(PaymentDto::status), PaymentStatus.APPROVED)
                                 .create();

        when(paymentService.findByStatus(PaymentStatus.APPROVED)).thenReturn(List.of(d1, d2));

        // when & then
        mockMvc.perform(get("/payments/statuses")
                                .param("status", "APPROVED")
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].guid").value(d1.guid().toString().toLowerCase()))
               .andExpect(jsonPath("$[1].guid").value(d2.guid().toString().toLowerCase()));
    }

    @Test
    @DisplayName("GET /payments/statuses with no matching status returns empty list")
    void getByStatus_ReturnsEmptyListWhenNoneFound() throws Exception {
        // given
        when(paymentService.findByStatus(DECLINED)).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/payments/statuses")
                                .param("status", "DECLINED")
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(0));
    }

}
