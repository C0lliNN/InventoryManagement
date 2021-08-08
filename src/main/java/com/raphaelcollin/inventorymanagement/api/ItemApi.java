package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateItem;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchItems;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateItem;
import com.raphaelcollin.inventorymanagement.api.dto.out.Item;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.ItemService;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
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
        return itemService
                .findByQuery(searchItems.toDomain())
                .map(Item::fromDomain);
    }

    public Mono<Item> findById(final String itemId) {
        return itemService
                .findById(itemId)
                .map(Item::fromDomain);
    }

    public Mono<Item> save(final CreateItem createItem) {
        return validator
                .validate(createItem)
                .map(create -> create.toDomain(idGenerator.newId()))
                .flatMap(itemService::save)
                .map(Item::fromDomain).log();
    }

    public Mono<Void> updateById(final String itemId, final UpdateItem updateItem) {
        return validator
                .validate(updateItem)
                .flatMap(update -> itemService.findById(itemId).map(updateItem::toDomain).flatMap(itemService::save))
                .then();
    }

    public Mono<Void> deleteById(final String itemId) {
        return itemService.deleteById(itemId);
    }
}