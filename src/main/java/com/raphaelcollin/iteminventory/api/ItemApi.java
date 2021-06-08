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

    public Mono<Void> save(final CreateItem createItem) {
        return itemService.save(createItem.toDomain(idGenerator.newId()));
    }

    public Mono<Item> findById(final String itemId) {
        return itemService.findById(itemId);
    }

    public Flux<Item> findItems(final SearchItems searchItems) {
        return itemService.findByQuery(searchItems.toDomain());
    }

    public Mono<Void> updateById(final String itemId, final UpdateItem updateItem) {
        return itemService
                .findById(itemId)
                .map(updateItem::toDomain)
                .flatMap(itemService::save)
                .then();
    }
}
