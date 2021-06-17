package com.raphaelcollin.iteminventory.infrastructure.controller.v2.router;

import com.raphaelcollin.iteminventory.infrastructure.controller.v2.handler.ItemHandler;
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

@Configuration
public class ItemRouter {
    private static final String ROOT_ENDPOINT = "/api/v2/items";

    @Bean
    public RouterFunction<ServerResponse> itemsRouter(ItemHandler handler) {
        return RouterFunctions
                .route(
                        GET(ROOT_ENDPOINT),
                        handler::findAllItems
                )
                .andRoute(
                        GET(ROOT_ENDPOINT + "/{itemId}"),
                        handler::findItemById
                )
                .andRoute(
                        POST(ROOT_ENDPOINT).and(accept(MediaType.APPLICATION_JSON)),
                        handler::saveItem)
                .andRoute(
                        PATCH(ROOT_ENDPOINT + "/{itemId}").and(accept(MediaType.APPLICATION_JSON)),
                        handler::updateItemById
                )
                .andRoute(
                        DELETE(ROOT_ENDPOINT + "/{itemId}"),
                        handler::deleteItemById
                );
    }
}
