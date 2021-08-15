package com.raphaelcollin.inventorymanagement.domain.category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository {
    Flux<Category> findAll();
    Mono<Category> findById(final String categoryId);
    Mono<Void> save(final Category category);
    Mono<Boolean> deleteById(final String categoryId);
}
