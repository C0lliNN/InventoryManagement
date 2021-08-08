package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raphaelcollin.inventorymanagement.domain.exceptions.RequestValidationException;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionBody {
    private static final String GENERIC_SERVER_ERROR_BODY_MESSAGE = "There's been an unexpected error. Please, contact support.";

    String message;
    Iterable<ErrorDetail> details;

    @JsonIgnore
    HttpStatus status;

    public static ExceptionBody fromException(Throwable exception) {
        if (exception instanceof RequestValidationException) {
            log.warn("There were validation errors in the request.", exception);
            return fromException((RequestValidationException) exception);
        }

        if (exception instanceof EntityNotFoundException) {
            log.warn("An attempt to operate on an unknown entity was made.", exception);
            return fromMessage(exception.getMessage(), NOT_FOUND);
        }

        log.error("An unexpected error was thrown when performing the request: " + exception.getMessage(), exception);
        return fromMessage(GENERIC_SERVER_ERROR_BODY_MESSAGE, INTERNAL_SERVER_ERROR);
    }

    private static ExceptionBody fromException(RequestValidationException exception) {
        final List<ErrorDetail> errorDetails = exception
                .getErrors()
                .stream()
                .sorted(comparing(FieldError::getField))
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ExceptionBody("The given payload is invalid. Check the 'details' field.", errorDetails, BAD_REQUEST);
    }

    private static ExceptionBody fromMessage(String message, HttpStatus status) {
        return new ExceptionBody(message, Collections.emptyList(), status);
    }

    @Value
    public static class ErrorDetail {
        String field;
        String message;
    }
}