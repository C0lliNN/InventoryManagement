package com.raphaelcollin.inventorymanagement.domain;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;

import java.math.BigDecimal;

public class ProductFactoryForTests {

    private static final Faker FAKER = Faker.instance();

    public static Product newProductDomain() {
                return Product
                .builder()
                .id(FAKER.internet().uuid())
                .sku(FAKER.lorem().fixedString(8))
                .name(FAKER.commerce().productName())
                .description(FAKER.lorem().sentence())
                .price(BigDecimal.valueOf(FAKER.random().nextInt(12, 100)))
                .quantity(FAKER.random().nextInt(1, 1000))
                .imageIdentifier(FAKER.internet().uuid())
                .category(CategoryFactoryForTests.newCategoryDomain())
                .build();
    }

    public static ProductDocument newProductDocument() {
        return new ProductDocument(
                FAKER.internet().uuid(),
                FAKER.lorem().fixedString(8),
                FAKER.commerce().productName(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                CategoryFactoryForTests.newCategoryDocument()
        );
    }

    public static CreateProduct newCreateProductDto() {
        return new CreateProduct(
                FAKER.commerce().productName(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                FAKER.internet().uuid()
        );
    }

    public static UpdateProduct newUpdateProductDto() {
        return new UpdateProduct(
                FAKER.commerce().productName(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid()
        );
    }

    public static SearchProducts newSearchProductsDto() {
        return new SearchProducts(
                FAKER.commerce().productName(),
                FAKER.random().nextInt(2, 10),
                FAKER.internet().uuid()
        );
    }
}
