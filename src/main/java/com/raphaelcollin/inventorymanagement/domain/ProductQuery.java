package com.raphaelcollin.inventorymanagement.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ProductQuery {
    String title;
    Integer minQuantity;

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<Integer> getMinQuantity() {
        return Optional.ofNullable(minQuantity);
    }
}
