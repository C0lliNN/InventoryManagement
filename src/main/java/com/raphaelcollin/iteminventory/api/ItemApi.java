package com.raphaelcollin.iteminventory.api;

import com.raphaelcollin.iteminventory.api.dto.in.CreateItem;
import com.raphaelcollin.iteminventory.api.dto.in.SearchItems;
import com.raphaelcollin.iteminventory.api.dto.in.UpdateItem;
import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemService;
import com.raphaelcollin.iteminventory.domain.common.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ItemApi {
    private final ItemService itemService;
    private final IdGenerator idGenerator;
    private final RequestValidator validator;

    public Flux<Item> findItems(final SearchItems searchItems) {
        return itemService.findByQuery(searchItems.toDomain());
    }

    public Mono<Item> findById(final String itemId) {
        return itemService.findById(itemId);
    }

    public Mono<Void> save(final CreateItem createItem) {
        return validator
                .validate(createItem)
                .map(create -> itemService.save(create.toDomain(idGenerator.newId())))
                .then();
    }

    public Mono<Void> updateById(final String itemId, final UpdateItem updateItem) {
        return validator
                .validate(updateItem)
                .flatMap(update -> itemService.findById(itemId).map(updateItem::toDomain).flatMap(itemService::save));
    }

    public Mono<Void> deleteById(final String itemId) {
        return itemService.deleteById(itemId);
    }
}
