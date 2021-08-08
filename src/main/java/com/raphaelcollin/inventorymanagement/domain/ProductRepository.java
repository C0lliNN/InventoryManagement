package com.raphaelcollin.inventorymanagement.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Flux<Product> findByQuery(final ProductQuery query);
    Mono<Product> findById(final String productId);
    Mono<Void> save(final Product product);
    Mono<Boolean> deleteById(final String productId);
}
