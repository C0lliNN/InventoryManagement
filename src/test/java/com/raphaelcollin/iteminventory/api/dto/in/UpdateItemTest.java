package com.raphaelcollin.iteminventory.api.dto.in;

import com.github.javafaker.Faker;
import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateItemTest {

    @Nested
    @DisplayName("method: toDomain(Item)")
    class ToDomainMethod {
        private final Item ITEM = ItemFactoryForTests.newItemDomain();
        private final Faker FAKER = Faker.instance();


        @Test
        @DisplayName("when all fields are filled, then it should return new item with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewItemWithTheGivenFields() {
            final String title = FAKER.name().title();
            final String description = FAKER.lorem().sentence();
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final int quantity = FAKER.random().nextInt(1, 100);

            final var updateItem = new UpdateItem(
                    title,
                    description,
                    price,
                    quantity
            );

            final var expectedItem = ITEM.toBuilder()
                    .title(title)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .build();

            final var actualItem = updateItem.toDomain(ITEM);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        @Test
        @DisplayName("when only the title is filled, then it should only update it")
        void whenOnlyTheTitleIsFilled_shouldOnlyUpdateIt() {
            final String title = FAKER.name().title();

            final var updateItem = new UpdateItem(
                    title,
                    null,
                    null,
                    null
            );

            final var expectedItem = ITEM.toBuilder().title(title).build();
            final var actualItem = updateItem.toDomain(ITEM);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        @Test
        @DisplayName("when only the description is filled, then it should only update it")
        void whenOnlyTheDescriptionIsFilled_shouldOnlyUpdateIt() {
            final String description = FAKER.lorem().sentence();

            final var updateItem = new UpdateItem(
                    null,
                    description,
                    null,
                    null
            );

            final var expectedItem = ITEM.toBuilder().description(description).build();
            final var actualItem = updateItem.toDomain(ITEM);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        @Test
        @DisplayName("when only the price is filled, then it should only update it")
        void whenOnlyThePriceIsFilled_shouldOnlyUpdateIt() {
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));

            final var updateItem = new UpdateItem(
                    null,
                    null,
                    price,
                    null
            );

            final var expectedItem = ITEM.toBuilder().price(price).build();
            final var actualItem = updateItem.toDomain(ITEM);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        @Test
        @DisplayName("when only the quantity is filled, then it should only update it")
        void whenOnlyTheQuantityIsFilled_shouldOnlyUpdateIt() {
            final int quantity = FAKER.random().nextInt(1, 10);

            final var updateItem = new UpdateItem(
                    null,
                    null,
                    null,
                    quantity
            );

            final var expectedItem = ITEM.toBuilder().quantity(quantity).build();
            final var actualItem = updateItem.toDomain(ITEM);

            assertThat(actualItem).isEqualTo(expectedItem);
        }
    }
}