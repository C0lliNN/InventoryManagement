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
            @DisplayName("when called and 'sku' is null, then it should throw a NullPointerException")
            void whenCalledAndSkuIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().sku(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("sku is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'name' is null, then it should throw a NullPointerException")
            void whenCalledAndNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().name(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("name is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'description' is null, then it should throw a NullPointerException")
            void whenCalledAndDescriptionIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().description(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("description is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'quantity' is null, then it should throw a NullPointerException")
            void whenCalledAndQuantityIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().quantity(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("quantity is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'imageIdentifier' is null, then it should throw a NullPointerException")
            void whenCalledAndImageIdentifierIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().imageIdentifier(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("imageIdentifier is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'category' is null, then it should throw a NullPointerException")
            void whenCalledAndCategoryIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> Product.toBuilder().category(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("category is marked non-null but is null");
            }
        }
    }
}