package com.raphaelcollin.inventorymanagement.infrastructure.rest.router;

import com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation.StorageEndpointDocumentation;
import com.raphaelcollin.inventorymanagement.infrastructure.rest.handler.StorageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class StorageRouter {
    private static final String ROOT_ENDPOINT = "/api/v1/storage";

    @Bean
    @StorageEndpointDocumentation
    public RouterFunction<ServerResponse> storageRouterFunction(StorageHandler handler) {
        return RouterFunctions
                .route(POST(ROOT_ENDPOINT), handler::generatePreSignedUrlForUpload);
    }
}
