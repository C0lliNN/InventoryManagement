package com.raphaelcollin.iteminventory.domain;

import com.github.javafaker.Faker;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;

import java.math.BigDecimal;

public class ItemFactoryForTests {

    private static final Faker FAKER = Faker.instance();

    public static Item newItemDomain() {
        return Item
                .builder()
                .id(FAKER.internet().uuid())
                .title(FAKER.name().title())
                .description(FAKER.lorem().sentence())
                .price(BigDecimal.valueOf(FAKER.random().nextInt(12, 100)))
                .quantity(FAKER.random().nextInt(1, 1000))
                .build();
    }

    public static ItemDocument newItemDocument() {
        return new ItemDocument(
                FAKER.internet().uuid(),
                FAKER.name().title(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000)
        );
    }
}
