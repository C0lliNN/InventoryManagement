package com.raphaelcollin.inventorymanagement.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Item {
    @NonNull
    String id;
    @NonNull
    String title;
    @NonNull
    String description;
    @NonNull
    BigDecimal price;
    int quantity;
}
