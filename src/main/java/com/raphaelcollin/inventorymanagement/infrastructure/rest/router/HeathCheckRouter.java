package com.raphaelcollin.inventorymanagement.infrastructure.rest.router;

import com.raphaelcollin.inventorymanagement.infrastructure.rest.handler.HealthCheckHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@AllArgsConstructor
public class HeathCheckRouter {
    private HealthCheckHandler handler;

    @Bean
    public RouterFunction<ServerResponse> heathCheckRouterFunction() {
        return RouterFunctions.route(GET("/"), (r) -> handler.handleHealthCheck());
    }
}
