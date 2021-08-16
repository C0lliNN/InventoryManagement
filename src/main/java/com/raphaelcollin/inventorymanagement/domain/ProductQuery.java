package com.raphaelcollin.inventorymanagement.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class ProductQuery {
    String name;
    Integer minQuantity;
    String categoryId;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<Integer> getMinQuantity() {
        return Optional.ofNullable(minQuantity);
    }

    public Optional<String> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }
}
