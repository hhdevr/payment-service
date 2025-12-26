package com.iprody.paymentserviceapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ExceptionMessageModel {

    private String message;

    @Override
    public String toString() {
        return "{\"message\":\"" + message + "\"}";
    }

}
