package com.raphaelcollin.inventorymanagement.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Nested
    @DisplayName("method: CategoryBuilder")
    class CategoryBuilderClass {

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {
            private final Category category = CategoryFactoryForTests.newCategoryDomain();

            @Test
            @DisplayName("when called and 'id' is null, then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> category.toBuilder().id(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("id is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'name' is null, then it should throw a NullPointerException")
            void whenCalledAndNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> category.toBuilder().name(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("name is marked non-null but is null");
            }
        }

    }
}