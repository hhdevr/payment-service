package com.iprody.paymentserviceapp.controller;

import com.chaykin.paymentserviceapi.AsyncSender;
import com.chaykin.paymentserviceapi.model.XPaymentAdapterRequestMessage;
import com.iprody.paymentserviceapp.AbstractPostgresIntegrationTest;
import com.iprody.paymentserviceapp.TestJwtFactory;
import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.model.Payment;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.persistence.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AsyncSender<XPaymentAdapterRequestMessage> xPaymentAdapterMessageProducer;

    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        mockMvc.perform(get("/payments")
                                .param("page", "0")
                                .param("size", "10")
                                .with(TestJwtFactory.jwtWithRoles("testuser", "USER", "READER"))
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(3)))
               .andExpect(jsonPath("$[*].guid", containsInAnyOrder("00000000-0000-0000-0000-000000000001",
                                                                   "00000000-0000-0000-0000-000000000002",
                                                                   "00000000-0000-0000-0000-000000000003")));
    }

    @Test
    @Transactional
    @Rollback
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        PaymentDto dto = new PaymentDto(null,
                                        UUID.randomUUID(),
                                        new BigDecimal("123.45"),
                                        "EUR",
                                        UUID.randomUUID(),
                                        PaymentStatus.PENDING,
                                        "note",
                                        OffsetDateTime.now(),
                                        OffsetDateTime.now());

        String json = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(post("/payments")
                                                  .with(TestJwtFactory.jwtWithRoles("testuser", "USER"))
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(json))
                                 .andExpect(status().isCreated())
                                 .andExpect(jsonPath("$.guid").exists())
                                 .andExpect(jsonPath("$.currency").value("EUR"))
                                 .andExpect(jsonPath("$.amount").value(123.45))
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();
        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.guid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        UUID existingId =
                UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get("/payments/" + existingId)
                                .with(TestJwtFactory.jwtWithRoles("testuser", "USER"))
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.guid").value(existingId.toString()))
               .andExpect(jsonPath("$.currency").value("EUR"))
               .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void shouldReturn404ForNonexistentPayment() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get("/payments/" + nonexistentId)
                                .with(TestJwtFactory.jwtWithRoles("testuser", "USER"))
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.message").value("Payment id=" + nonexistentId + " does not exist"));
    }
}