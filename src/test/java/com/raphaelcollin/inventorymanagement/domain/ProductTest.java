package com.raphaelcollin.inventorymanagement.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Nested
    @DisplayName("class: ProductBuilder")
    class ProductBuilderClass {
        private final Product Product = ProductFactoryForTests.newProductDomain();

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {

            @Test
            @DisplayName("when called and 'id' is null, then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().id(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("id is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'title' is null, then it should throw a NullPointerException")
            void whenCalledAndTitleIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().title(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("title is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'description' is null, then it should throw a NullPointerException")
            void whenCalledAndDescriptionIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().description(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("description is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'price' is null, then it should throw a NullPointerException")
            void whenCalledAndPriceIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().price(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("price is marked non-null but is null");
            }

        }
    }
}