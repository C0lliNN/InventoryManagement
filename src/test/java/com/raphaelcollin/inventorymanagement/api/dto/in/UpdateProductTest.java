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
            final String name = FAKER.commerce().productName();
            final String sku = FAKER.lorem().fixedString(8);
            final String description = FAKER.lorem().sentence();
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final int quantity = FAKER.random().nextInt(1, 100);
            final String imageIdentifier = FAKER.internet().uuid();

            final var updateProduct = new UpdateProduct(
                    name,
                    sku,
                    description,
                    price,
                    quantity,
                    imageIdentifier
            );

            final var expectedProduct = PRODUCT.toBuilder()
                    .name(name)
                    .sku(sku)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .imageIdentifier(imageIdentifier)
                    .build();

            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the name is filled, then it should only update it")
        void whenOnlyTheNameIsFilled_shouldOnlyUpdateIt() {
            final String name = FAKER.commerce().productName();

            final var updateProduct = new UpdateProduct(
                    name,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().name(name).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the sku is filled, then it should only update it")
        void whenOnlyTheSkuIsFilled_shouldOnlyUpdateIt() {
            final String sku = FAKER.lorem().fixedString(8);

            final var updateProduct = new UpdateProduct(
                    null,
                    sku,
                    null,
                    null,
                    null,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().sku(sku).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the description is filled, then it should only update it")
        void whenOnlyTheDescriptionIsFilled_shouldOnlyUpdateIt() {
            final String description = FAKER.lorem().sentence();

            final var updateProduct = new UpdateProduct(
                    null,
                    null,
                    description,
                    null,
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
                    null,
                    price,
                    null,
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
                    null,
                    quantity,
                    null
            );

            final var expectedProduct = PRODUCT.toBuilder().quantity(quantity).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        @Test
        @DisplayName("when only the imageIdentifier is filled, then it should only update it")
        void whenOnlyTheImageIdentifierIsFilled_shouldOnlyUpdateIt() {
            final String imageIdentifier = FAKER.internet().uuid();

            final var updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    null,
                    imageIdentifier
            );

            final var expectedProduct = PRODUCT.toBuilder().imageIdentifier(imageIdentifier).build();
            final var actualProduct = updateProduct.toDomain(PRODUCT);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }
    }
}