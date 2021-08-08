package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.Item;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ItemDocument;
import org.springframework.stereotype.Component;

@Component
public class ItemSerializer implements DocumentSerializer<Item, ItemDocument> {

    @Override
    public Item fromDocument(final ItemDocument document) {
        return Item
                .builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .build();
    }

    @Override
    public ItemDocument toDocument(final Item domain) {
        return new ItemDocument(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getQuantity()
        );
    }
}
