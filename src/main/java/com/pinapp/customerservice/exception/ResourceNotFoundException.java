package com.pinapp.customerservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomerException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}