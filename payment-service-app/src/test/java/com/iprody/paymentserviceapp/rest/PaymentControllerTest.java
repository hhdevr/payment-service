package com.iprody.paymentserviceapp.rest;

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
import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /payments should return list of two PaymentDto")
    void findAll_ReturnsListOfTwoPayments() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();

        PaymentDto d1 = new PaymentDto(
                1L,
                BigDecimal.valueOf(100.50),
                "First",
                now,
                now
        );

        PaymentDto d2 = new PaymentDto(
                2L,
                BigDecimal.valueOf(200.75),
                "Second",
                now,
                now
        );

        when(paymentService.findAll()).thenReturn(List.of(d1, d2));

        // when & then
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
        Long id = 10L;
        LocalDateTime now = LocalDateTime.now();

        PaymentDto dto = new PaymentDto(
                id,
                BigDecimal.TEN,
                "Test payment",
                now,
                now
        );

        when(paymentService.findById(id)).thenReturn(Optional.of(dto));

        // when & then
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

        // when & then
        mockMvc.perform(get("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}
