package com.iprody.paymentserviceapp.rest;

import com.iprody.paymentserviceapp.converter.PaymentConverter;
import com.iprody.paymentserviceapp.rest.model.PaymentDto;
import com.iprody.paymentserviceapp.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = "/payments",
                produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class PaymentController {

    private final PaymentService service;

    private final PaymentConverter converter;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ok(converter.convert(service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return service.findById(id)
                      .map(payment -> ok(converter.convert(payment)))
                      .orElseGet(() -> notFound().build());
    }

}
