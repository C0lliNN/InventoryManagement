package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import org.springframework.stereotype.Component;

@Component
public class ProductSerializer implements DocumentSerializer<Product, ProductDocument> {

    @Override
    public Product fromDocument(final ProductDocument document) {
        return Product
                .builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .imageIdentifier(document.getImageIdentifier())
                .build();
    }

    @Override
    public ProductDocument toDocument(final Product domain) {
        return new ProductDocument(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getQuantity(),
                domain.getImageIdentifier()
        );
    }
}
