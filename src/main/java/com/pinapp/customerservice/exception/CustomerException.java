package com.pinapp.customerservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomerException extends RuntimeException {

    private final HttpStatus status;

    public CustomerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}