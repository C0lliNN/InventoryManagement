package com.raphaelcollin.iteminventory.infrastructure.rest.exception;

import com.raphaelcollin.iteminventory.api.dto.out.ExceptionBody;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RouteExceptionHandler extends AbstractErrorWebExceptionHandler {

    public RouteExceptionHandler(ErrorAttributes errorAttributes,
                                 ApplicationContext applicationContext,
                                 ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);

        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(),
                this::renderErrorResponse
        );
    }

    private @NonNull Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        final var exception = ExceptionBody.fromException(getError(request));

        return ServerResponse
                .status(exception.getStatus())
                .bodyValue(exception);
    }
}