package com.raphaelcollin.iteminventory.api;

import com.raphaelcollin.iteminventory.api.dto.in.CreateItem;
import com.raphaelcollin.iteminventory.domain.ItemService;
import com.raphaelcollin.iteminventory.domain.common.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ItemApi {
    private final ItemService itemService;
    private final IdGenerator idGenerator;

    public Mono<Void> save(final CreateItem createItem) {
        return itemService.save(createItem.toDomain(idGenerator.newId()));
    }
}
