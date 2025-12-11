package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.controller.model.PaymentDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilter;
import com.iprody.paymentserviceapp.persistence.QPaymentFilter;
import com.iprody.paymentserviceapp.persistence.model.PaymentStatus;
import com.iprody.paymentserviceapp.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.domain.Sort.unsorted;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = "/payments",
                produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable UUID id) {
        return ok(service.getById(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<PaymentDto>> getByStatus(@RequestParam PaymentStatus status) {
        return ok(service.findByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PaymentDto>> searchPayments(@ModelAttribute PaymentFilter filter,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @RequestParam(defaultValue = "createdAt") String sortBy,
                                                           @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = unsorted();
        if (StringUtils.hasText(sortBy)) {
            sort = direction.equalsIgnoreCase("desc")
                   ? Sort.by(sortBy).descending()
                   : Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return ok(service.searchPaged(filter, pageable));
    }

    @GetMapping("/search-q")
    public ResponseEntity<Page<PaymentDto>> searchPayments(@ModelAttribute QPaymentFilter filter) {
        return ok(service.searchQPaged(filter));
    }
}
