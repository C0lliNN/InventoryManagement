package com.raphaelcollin.inventorymanagement.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository {
    Flux<Item> findByQuery(final ItemQuery query);
    Mono<Item> findById(final String itemId);
    Mono<Void> save(final Item item);
    Mono<Boolean> deleteById(final String itemId);
}
