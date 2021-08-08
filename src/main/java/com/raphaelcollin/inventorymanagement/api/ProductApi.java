package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.out.Product;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.ProductService;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ProductApi {
    private final ProductService productService;
    private final IdGenerator idGenerator;
    private final RequestValidator validator;

    public Flux<Product> findProducts(final SearchProducts searchProducts) {
        return productService
                .findByQuery(searchProducts.toDomain())
                .map(Product::fromDomain);
    }

    public Mono<Product> findById(final String productId) {
        return productService
                .findById(productId)
                .map(Product::fromDomain);
    }

    public Mono<Product> save(final CreateProduct createProduct) {
        return validator
                .validate(createProduct)
                .map(create -> create.toDomain(idGenerator.newId()))
                .flatMap(productService::save)
                .map(Product::fromDomain).log();
    }

    public Mono<Void> updateById(final String productId, final UpdateProduct updateProduct) {
        return validator
                .validate(updateProduct)
                .flatMap(update -> productService.findById(productId).map(updateProduct::toDomain).flatMap(productService::save))
                .then();
    }

    public Mono<Void> deleteById(final String productId) {
        return productService.deleteById(productId);
    }
}
