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
    private final ProductService productService;

    public @NonNull Mono<ServerResponse> findAllProducts(ServerRequest request) {
        return productService
                .findProducts(SearchProducts.fromQueryParams(request.queryParams()))
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }

    public @NonNull Mono<ServerResponse> findProductById(ServerRequest request) {
        return productService
                .findById(request.pathVariable("productId"))
                .flatMap(product -> ServerResponse.ok().bodyValue(product));
    }

    public @NonNull Mono<ServerResponse> saveProduct(ServerRequest request) {
        final Function<Product, Mono<ServerResponse>> responseBuilder = product -> ServerResponse
                .created(URI.create(request.path() + "/" + product.getId()))
                .bodyValue(product);

        return request
                .bodyToMono(CreateProduct.class)
                .flatMap(productService::save)
                .flatMap(responseBuilder);
    }

    public @NonNull Mono<ServerResponse> updateProductById(ServerRequest request) {
        return request
                .bodyToMono(UpdateProduct.class)
                .flatMap(updateProduct -> productService.updateById(request.pathVariable("productId"), updateProduct))
                .then(ServerResponse.noContent().build());
    }

    public @NonNull Mono<ServerResponse> deleteProductById(ServerRequest request) {
        return productService
                .deleteById(request.pathVariable("productId"))
                .then(ServerResponse.noContent().build());
    }
}
