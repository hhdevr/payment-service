package com.iprody.paymentserviceapp.rest;

import com.iprody.paymentserviceapp.controller.PaymentController;
import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.exception.ErrorMessage;
import com.iprody.paymentserviceapp.exception.GlobalExceptionHandler;
import com.iprody.paymentserviceapp.exception.ServiceException;
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
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    private ObjectMapper objectMapper = new ObjectMapper();

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
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /payments should create and return new PaymentDto")
    void createPayment_CreatesSuccessfully() throws Exception {
        // given
        PaymentDto inputDto = Instancio.of(PaymentDto.class)
                                       .set(field(PaymentDto::guid), null)  // новый объект без ID
                                       .create();
        PaymentDto savedDto = Instancio.of(PaymentDto.class)
                                       .set(field(PaymentDto::guid), UUID.randomUUID())
                                       .create();

        when(paymentService.create(inputDto)).thenReturn(savedDto);

        // when & then
        mockMvc.perform(post("/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDto)))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.guid").value(savedDto.guid().toString().toLowerCase()));
    }

    @Test
    @DisplayName("DELETE /payments/{id} should return 204 No Content when deleted")
    void deletePayment_DeletesSuccessfully() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        doNothing().when(paymentService).delete(id);

        // when & then
        mockMvc.perform(delete("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /payments/{id} should return 404 when payment not exists")
    void deletePayment_ThrowsNotFound_WhenNotExists() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        doThrow(new ServiceException(ErrorMessage.PAYMENT_NOT_EXIST, id))
                .when(paymentService).delete(id);

        // when & then
        mockMvc.perform(delete("/payments/{id}", id)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /payments/{id} should update existing payment")
    void updatePayment_UpdatesSuccessfully() throws Exception {
        // given
        PaymentDto inputDto = Instancio.create(PaymentDto.class);
        PaymentDto updatedDto = Instancio.of(PaymentDto.class)
                                         .set(field(PaymentDto::guid), inputDto.guid())
                                         .set(field(PaymentDto::note), "updated note")
                                         .create();

        when(paymentService.update(inputDto)).thenReturn(updatedDto);

        // when & then
        mockMvc.perform(put("/payments/{id}", inputDto.guid())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDto)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.guid").value(inputDto.guid().toString().toLowerCase()))
               .andExpect(jsonPath("$.note").value("updated note"));
    }

    @Test
    @DisplayName("PUT /payments/{id} should return 404 when payment not exists")
    void updatePayment_ThrowsNotFound_WhenNotExists() throws Exception {
        // given
        PaymentDto inputDto = Instancio.create(PaymentDto.class);
        doThrow(new ServiceException(ErrorMessage.PAYMENT_NOT_EXIST, inputDto.guid()))
                .when(paymentService).update(inputDto);

        // when & then
        mockMvc.perform(put("/payments/{id}", inputDto.guid())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDto)))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /payments/{id} should update note successfully")
    void updateNotePatch_UpdatesNoteSuccessfully() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        String newNote = "patch note";
        when(paymentService.updateNote(id, newNote)).thenReturn(true);

        // when & then
        mockMvc.perform(patch("/payments/{id}", id)
                                .param("note", newNote)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("PATCH /payments/{id} should return 404 when payment not exists")
    void updateNotePatch_ThrowsNotFound_WhenNotExists() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        String newNote = "patch note";
        doThrow(new ServiceException(ErrorMessage.PAYMENT_NOT_EXIST, id))
                .when(paymentService).updateNote(id, newNote);

        // when & then
        mockMvc.perform(patch("/payments/{id}", id)
                                .param("note", newNote)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

}
