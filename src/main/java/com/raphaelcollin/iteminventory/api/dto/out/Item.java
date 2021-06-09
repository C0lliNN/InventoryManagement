package com.raphaelcollin.iteminventory.api.dto.out;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Item {
    String id;
    String title;
    String description;
    BigDecimal price;
    int quantity;

    public static Item fromDomain(final com.raphaelcollin.iteminventory.domain.Item item) {
        return new Item(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                item.getQuantity()
        );
    }
}
