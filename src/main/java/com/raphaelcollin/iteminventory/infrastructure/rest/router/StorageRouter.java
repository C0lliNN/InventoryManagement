package com.raphaelcollin.iteminventory.infrastructure.rest.router;

import com.raphaelcollin.iteminventory.infrastructure.rest.handler.StorageHandler;
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
    public RouterFunction<ServerResponse> storageRouterFunction(StorageHandler handler) {
        return RouterFunctions
                .route(POST(ROOT_ENDPOINT), handler::generatePreSignedUrlForUpload);
    }
}
