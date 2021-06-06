package com.raphaelcollin.iteminventory.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    @Nested
    @DisplayName("class: ItemBuilder")
    class ItemBuilderClass {
        private final Item ITEM = ItemFactoryForTests.newItemDomain();

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {

            @Test
            @DisplayName("when called and 'id' is null, then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> ITEM.toBuilder().id(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("id is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'title' is null, then it should throw a NullPointerException")
            void whenCalledAndTitleIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> ITEM.toBuilder().title(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("title is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'description' is null, then it should throw a NullPointerException")
            void whenCalledAndDescriptionIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> ITEM.toBuilder().description(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("description is marked non-null but is null");
            }

            @Test
            @DisplayName("when called and 'price' is null, then it should throw a NullPointerException")
            void whenCalledAndPriceIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> ITEM.toBuilder().price(null).build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("price is marked non-null but is null");
            }

        }
    }
}