package com.raphaelcollin.inventorymanagement.domain.exceptions;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

public class RequestValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    public List<FieldError> errors;

    public RequestValidationException(final List<FieldError> errors) {
        super(errors.toString());
        this.errors = errors;
    }
}
