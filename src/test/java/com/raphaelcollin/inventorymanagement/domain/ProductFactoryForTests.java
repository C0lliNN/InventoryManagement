package com.raphaelcollin.inventorymanagement.domain;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;

import java.math.BigDecimal;

public class ProductFactoryForTests {

    private static final Faker FAKER = Faker.instance();

    public static Product newProductDomain() {
        return Product
                .builder()
                .id(FAKER.internet().uuid())
                .title(FAKER.name().title())
                .description(FAKER.lorem().sentence())
                .price(BigDecimal.valueOf(FAKER.random().nextInt(12, 100)))
                .quantity(FAKER.random().nextInt(1, 1000))
                .imageIdentifier(FAKER.internet().uuid())
                .build();
    }

    public static ProductDocument newProductDocument() {
        return new ProductDocument(
                FAKER.internet().uuid(),
                FAKER.name().title(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid()
        );
    }

    public static ProductQuery newProductQuery() {
        return ProductQuery
                .builder()
                .title(FAKER.name().title())
                .minQuantity(FAKER.random().nextInt(5, 10))
                .build();
    }

    public static CreateProduct newCreateProductDto() {
        return new CreateProduct(
                FAKER.name().title(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid()
        );
    }

    public static UpdateProduct newUpdateProductDto() {
        return new UpdateProduct(
                FAKER.name().title(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid()
        );
    }

    public static SearchProducts newSearchProductsDto() {
        return new SearchProducts(FAKER.name().title(), FAKER.random().nextInt(2, 10));
    }
}
