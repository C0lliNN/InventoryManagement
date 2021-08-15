package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCategoryTest {

    @Nested
    @DisplayName("method: toDomain(String)")
    class ToDomainMethod {

        @Test
        @DisplayName("when called, then it should create the correct domain object")
        void whenCalled_shouldCreateTheCorrectDomainObject() {
            final String categoryId = UUID.randomUUID().toString();
            final CreateCategory createCategory = CategoryFactoryForTests.newCreateCategory();

            final Category expected = Category
                    .builder()
                    .id(categoryId)
                    .name(createCategory.getName())
                    .build();

            final Category actual = createCategory.toDomain(categoryId);

            assertThat(actual).isEqualTo(expected);
        }
    }
}