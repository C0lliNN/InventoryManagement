package com.raphaelcollin.inventorymanagement.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Product {
    @NonNull
    String id;
    @NonNull
    String name;
    @NonNull
    String sku;
    @NonNull
    String description;
    @NonNull
    BigDecimal price;
    @NonNull
    Integer quantity;
    @NonNull
    String imageIdentifier;
}
