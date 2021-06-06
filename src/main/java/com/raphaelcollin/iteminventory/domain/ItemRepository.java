package com.raphaelcollin.iteminventory.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository {
    Flux<Item> findAllItems();
    Mono<Item> findItemById(final String itemId);
    Mono<Void> save(final Item item);
}
