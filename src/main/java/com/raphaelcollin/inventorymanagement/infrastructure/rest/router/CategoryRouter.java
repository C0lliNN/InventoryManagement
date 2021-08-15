package com.raphaelcollin.inventorymanagement.infrastructure.rest.router;

import com.raphaelcollin.inventorymanagement.infrastructure.rest.handler.CategoryHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@AllArgsConstructor
@Configuration
public class CategoryRouter {
    private final CategoryHandler categoryHandler;
    private static final String BASE_URL = "/api/v1/categories";

    @Bean
    public RouterFunction<ServerResponse> categoryRouterFunction() {
        return RouterFunctions
                .route(GET(BASE_URL), categoryHandler::findAllCategories)
                .andRoute(POST(BASE_URL).and(accept(MediaType.APPLICATION_JSON)), categoryHandler::saveCategory)
                .andRoute(PATCH(BASE_URL + "/{categoryId}").and(accept(MediaType.APPLICATION_JSON)), categoryHandler::updateCategoryById)
                .andRoute(DELETE(BASE_URL + "/{categoryId}").and(accept(MediaType.APPLICATION_JSON)), categoryHandler::deleteCategoryById);
    }
}
