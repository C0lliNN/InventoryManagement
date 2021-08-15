package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateCategoryTest {

    @Nested
    @DisplayName("method: toDomain(Category)")
    class ToDomainMethod {
        final Category category = CategoryFactoryForTests.newCategoryDomain();

        @Test
        @DisplayName("when no field is present, then it should not update anything")
        void whenNoFieldIsPresent_shouldNotUpdateAnything() {
            final UpdateCategory updateCategory = new UpdateCategory(null);

            assertThat(updateCategory.toDomain(category)).isEqualTo(category);
        }

        @Test
        @DisplayName("when only the name field is present, then it should only update it")
        void whenOnlyTheNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String name = UUID.randomUUID().toString();

            final UpdateCategory updateCategory = new UpdateCategory(name);

            final Category expected = category.toBuilder().name(name).build();
            final Category actual = updateCategory.toDomain(category);

            assertThat(actual).isEqualTo(expected);
        }
    }
}