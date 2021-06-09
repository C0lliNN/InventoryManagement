package com.raphaelcollin.iteminventory.infrastructure.controller.v1.exception;

import com.raphaelcollin.iteminventory.api.dto.out.ExceptionBody;
import com.raphaelcollin.iteminventory.domain.exceptions.EntityNotFoundException;
import com.raphaelcollin.iteminventory.domain.exceptions.RequestValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleEntityNotFoundException(final EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionBody.fromException(e));
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ExceptionBody> handleRequestValidationException(final RequestValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionBody.fromException(e));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionBody> handleGeneralError(final Throwable e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionBody.fromException(e));
    }
}
