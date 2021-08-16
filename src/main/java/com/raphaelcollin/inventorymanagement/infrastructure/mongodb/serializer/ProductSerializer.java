package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ProductSerializer implements DocumentSerializer<Product, ProductDocument> {
    private final DocumentSerializer<Category, CategoryDocument> categorySerializer;

    @Override
    public Product fromDocument(final ProductDocument document) {
        return Product
                .builder()
                .id(document.getId())
                .sku(document.getSku())
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .imageIdentifier(document.getImageIdentifier())
                .category(categorySerializer.fromDocument(document.getCategory()))
                .build();
    }

    @Override
    public ProductDocument toDocument(final Product domain) {
        return new ProductDocument(
                domain.getId(),
                domain.getSku(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getQuantity(),
                domain.getImageIdentifier(),
                categorySerializer.toDocument(domain.getCategory())
        );
    }
}
