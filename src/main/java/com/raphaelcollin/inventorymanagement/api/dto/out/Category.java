package com.raphaelcollin.inventorymanagement.api.dto.out;

import lombok.Value;

@Value
public class Category {
    String id;
    String name;

    public static Category fromDomain(final com.raphaelcollin.inventorymanagement.domain.category.Category category) {
        return new Category(category.getId(), category.getName());
    }
}
