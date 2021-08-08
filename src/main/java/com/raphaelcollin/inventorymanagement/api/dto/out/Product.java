package com.raphaelcollin.inventorymanagement.api.dto.out;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Product {
    String id;
    String title;
    String description;
    BigDecimal price;
    int quantity;

    public static Product fromDomain(final com.raphaelcollin.inventorymanagement.domain.Product product) {
        return new Product(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
