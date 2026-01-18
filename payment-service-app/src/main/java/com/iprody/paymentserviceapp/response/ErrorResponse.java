package com.iprody.paymentserviceapp.response;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Integer error;
    private final String message;

    public ErrorResponse(Integer error, String message) {
        this.error = error;
        this.message = message;
    }
}
