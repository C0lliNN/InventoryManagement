package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class HealthCheckHandler {

    // This can be replaced with an actual implementation
    public Mono<ServerResponse> handleHealthCheck() {
        Map<String, String> response = Map.of("status", "OK", "message", "Hello World!");

        return ServerResponse
                .ok()
                .bodyValue(response);
    }
}
