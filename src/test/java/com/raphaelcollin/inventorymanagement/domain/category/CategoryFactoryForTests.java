package com.raphaelcollin.inventorymanagement.domain.category;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;

public class CategoryFactoryForTests {
    private static final Faker FAKER = Faker.instance();

    public static Category newCategoryDomain() {
        return Category
                .builder()
                .id(FAKER.internet().uuid())
                .name(FAKER.commerce().department())
                .build();
    }

    public static CategoryDocument newCategoryDocument() {
        return new CategoryDocument(FAKER.internet().uuid(), FAKER.commerce().department());
    }
}
