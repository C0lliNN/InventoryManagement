package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateProductTest {

    @Nested
    @DisplayName("method: toDomain(Product)")
    class ToDomainMethod {
        private final Product PRODUCT = ProductFactoryForTests.newProductDomain();
        private final Faker FAKER = Faker.instance();


        @Test
        @DisplayName("when all fields are filled, then it should return new product with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewProductWithTheGivenFields() {
            final String title = FAKER.name().title();
            final String description = FAKER.lorem().sentence();
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final int quantity = FAKER.random().nextInt(1, 100);

            final var updateProduct = new UpdateProduct(
                    title,
                    description,
                    price,
                    quantity
            );

            final var expectedProduct = PRODUCT.toBuilder()
                    .title(title)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .build();

            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the title is filled, then it should only update it")
        void whenOnlyTheTitleIsFilled_shouldOnlyUpdateIt() {
            final String title = FAKER.name().title();

            final var updateProduct = new UpdateProduct(
                    title,
                    null,
                    null,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().title(title).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the description is filled, then it should only update it")
        void whenOnlyTheDescriptionIsFilled_shouldOnlyUpdateIt() {
            final String description = FAKER.lorem().sentence();

            final var updateProduct = new UpdateProduct(
                    null,
                    description,
                    null,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().description(description).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the price is filled, then it should only update it")
        void whenOnlyThePriceIsFilled_shouldOnlyUpdateIt() {
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));

            final var updateProduct = new UpdateProduct(
                    null,
                    null,
                    price,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().price(price).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the quantity is filled, then it should only update it")
        void whenOnlyTheQuantityIsFilled_shouldOnlyUpdateIt() {
            final int quantity = FAKER.random().nextInt(1, 10);

            final var updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    quantity
            );

            final var expectedProduct = PRODUCT.toBuilder().quantity(quantity).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }
    }
}