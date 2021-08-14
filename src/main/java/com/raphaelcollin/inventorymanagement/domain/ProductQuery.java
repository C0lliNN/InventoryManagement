package com.raphaelcollin.inventorymanagement.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ProductQuery {
    String name;
    Integer minQuantity;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<Integer> getMinQuantity() {
        return Optional.ofNullable(minQuantity);
    }
}
