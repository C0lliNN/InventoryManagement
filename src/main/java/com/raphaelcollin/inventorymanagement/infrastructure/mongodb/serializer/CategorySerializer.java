package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import org.springframework.stereotype.Component;

@Component
public class CategorySerializer implements DocumentSerializer<Category, CategoryDocument> {

    @Override
    public Category fromDocument(final CategoryDocument document) {
        return Category
                .builder()
                .id(document.getId())
                .name(document.getName())
                .build();
    }

    @Override
    public CategoryDocument toDocument(final Category category) {
        return new CategoryDocument(category.getId(), category.getName());
    }
}
