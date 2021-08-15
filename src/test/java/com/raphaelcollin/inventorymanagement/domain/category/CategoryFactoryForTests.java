package com.raphaelcollin.inventorymanagement.domain.category;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateCategory;
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

    public static CreateCategory newCreateCategory() {
        return new CreateCategory(FAKER.internet().uuid());
    }

    public static UpdateCategory newUpdateCategory() {
        return new UpdateCategory(FAKER.internet().uuid());
    }
}
