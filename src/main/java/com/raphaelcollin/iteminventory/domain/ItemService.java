package com.raphaelcollin.iteminventory.domain;

import com.raphaelcollin.iteminventory.domain.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Flux<Item> findByQuery(final ItemQuery query) {
        return itemRepository.findByQuery(query);
    }

    public Mono<Item> findById(final String itemId) {
        return itemRepository
                .findById(itemId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Item with ID %s was not found", itemId))));
    }

    public Mono<Item> save(final Item item) {
        return itemRepository.save(item).then(Mono.just(item));
    }

    public Mono<Void> deleteById(final String itemId) {
        return itemRepository
                .deleteById(itemId)
                .filter(result -> result)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Item with ID %s was not found", itemId))))
                .then();
    }
}
