package com.iprody.paymentserviceapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    // Common
    NULL_ID(HttpStatus.BAD_REQUEST, "ID cannot be null", 101),
    NEGATIVE_ID(HttpStatus.BAD_REQUEST, "ID cannot be 0 or negative", 102),

    // Payments
    PAYMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "Payment id=%s does not exist", 103),

    STATUS_NOT_MATCH(HttpStatus.BAD_REQUEST, "Payment with id=%s has incorrect status", 104);

    private final HttpStatus status;
    private final String message;
    private final int code;

}
