package com.raphaelcollin.iteminventory.infrastructure.rest.router;

import com.raphaelcollin.iteminventory.infrastructure.rest.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
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
    private static final String ROOT_ENDPOINT = "/api/v1/items";

    @Bean
    public RouterFunction<ServerResponse> itemsRouter(ItemHandler handler) {
        final RequestPredicate getItemsPredicate = GET(ROOT_ENDPOINT);
        final RequestPredicate getItemPredicate = GET(ROOT_ENDPOINT + "/{itemId}");
        final RequestPredicate postItemPredicate = POST(ROOT_ENDPOINT).and(accept(MediaType.APPLICATION_JSON));
        final RequestPredicate patchItemPredicate = PATCH(ROOT_ENDPOINT + "/{itemId}").and(accept(MediaType.APPLICATION_JSON));
        final RequestPredicate deleteItemPredicate = DELETE(ROOT_ENDPOINT + "/{itemId}");

        return RouterFunctions
                .route(getItemsPredicate, handler::findAllItems)
                .andRoute(getItemPredicate, handler::findItemById)
                .andRoute(postItemPredicate, handler::saveItem)
                .andRoute(patchItemPredicate, handler::updateItemById)
                .andRoute(deleteItemPredicate, handler::deleteItemById);
    }
}
