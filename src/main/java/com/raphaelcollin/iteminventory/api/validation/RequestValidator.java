package com.raphaelcollin.iteminventory.api.validation;

import com.raphaelcollin.iteminventory.domain.exceptions.RequestValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

import static java.util.stream.Collectors.toUnmodifiableList;

@AllArgsConstructor
@Component
public class RequestValidator {
    private final Validator validator;

    public <T> Mono<T> validate(T request) {
        return Mono.create(sink -> {
            final var errors = validator
                    .validate(request)
                    .stream()
                    .map(error -> new FieldError(request.getClass().getName(), error.getPropertyPath().toString(), error.getMessage()))
                    .collect(toUnmodifiableList());

            if (!errors.isEmpty()) {
                sink.error(new RequestValidationException(errors));
            }

            sink.success(request);
        });
    }

}
