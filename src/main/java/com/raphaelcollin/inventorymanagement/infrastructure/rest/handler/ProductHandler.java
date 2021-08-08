package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.raphaelcollin.inventorymanagement.api.ProductService;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.out.Product;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ProductHandler {
    private final ProductService productApi;

    public @NonNull Mono<ServerResponse> findAllProducts(ServerRequest request) {
        final Function<SearchProducts, Mono<ServerResponse>> responseBuilder = searchProducts -> ServerResponse
                .ok()
                .body(productApi.findProducts(searchProducts), Product.class);

        return Mono.just(request)
                .map(req -> SearchProducts.fromQueryParams(req.queryParams()))
                .flatMap(responseBuilder);
    }

    public @NonNull Mono<ServerResponse> findProductById(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(productApi.findById(request.pathVariable("productId")), Product.class);
    }

    public @NonNull Mono<ServerResponse> saveProduct(ServerRequest request) {
        final Function<Product, Mono<ServerResponse>> responseBuilder = product -> ServerResponse
                .created(URI.create("/api/v1/products/" + product.getId()))
                .bodyValue(product);

        return request
                .bodyToMono(CreateProduct.class)
                .flatMap(productApi::save)
                .flatMap(responseBuilder);
    }

    public @NonNull Mono<ServerResponse> updateProductById(ServerRequest request) {
        return request
                .bodyToMono(UpdateProduct.class)
                .flatMap(updateProduct -> productApi.updateById(request.pathVariable("productId"), updateProduct))
                .then(ServerResponse.noContent().build());
    }

    public @NonNull Mono<ServerResponse> deleteProductById(ServerRequest request) {
        return Mono.just(request)
                .flatMap(req -> productApi.deleteById(req.pathVariable("productId")))
                .then(ServerResponse.noContent().build());
    }
}
