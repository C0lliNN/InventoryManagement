package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.raphaelcollin.inventorymanagement.domain.storage.Image;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class Product {
    String id;
    String title;
    String description;
    BigDecimal price;
    int quantity;
    String imageUrl;

    public static Product from(final com.raphaelcollin.inventorymanagement.domain.Product product, Image image) {
        return new Product(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                image.getPreSignedUrl()
        );
    }
}
