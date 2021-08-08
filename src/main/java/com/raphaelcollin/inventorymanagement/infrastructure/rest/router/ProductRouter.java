package com.raphaelcollin.inventorymanagement.infrastructure.rest.router;

import com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation.ProductEndpointDocumentation;
import com.raphaelcollin.inventorymanagement.infrastructure.rest.handler.ProductHandler;
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
public class ProductRouter {
    private static final String ROOT_ENDPOINT = "/api/v1/products";

    @Bean
    @ProductEndpointDocumentation
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler handler) {
        final RequestPredicate getProductsPredicate = GET(ROOT_ENDPOINT);
        final RequestPredicate getProductPredicate = GET(ROOT_ENDPOINT + "/{productId}");
        final RequestPredicate postProductPredicate = POST(ROOT_ENDPOINT).and(accept(MediaType.APPLICATION_JSON));
        final RequestPredicate patchProductPredicate = PATCH(ROOT_ENDPOINT + "/{productId}").and(accept(MediaType.APPLICATION_JSON));
        final RequestPredicate deleteProductPredicate = DELETE(ROOT_ENDPOINT + "/{productId}");

        return RouterFunctions
                .route(getProductsPredicate, handler::findAllProducts)
                .andRoute(getProductPredicate, handler::findProductById)
                .andRoute(postProductPredicate, handler::saveProduct)
                .andRoute(patchProductPredicate, handler::updateProductById)
                .andRoute(deleteProductPredicate, handler::deleteProductById);
    }
}
