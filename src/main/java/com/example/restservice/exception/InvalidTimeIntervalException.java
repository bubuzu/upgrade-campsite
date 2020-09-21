package com.example.restservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidTimeIntervalException extends Exception {
    public InvalidTimeIntervalException(String message) {
        super(message);
    }
}
