package com.raphaelcollin.iteminventory.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ItemQuery {
    String title;
    Integer minQuantity;

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<Integer> getMinQuantity() {
        return Optional.ofNullable(minQuantity);
    }
}
