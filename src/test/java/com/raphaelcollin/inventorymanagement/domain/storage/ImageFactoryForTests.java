package com.raphaelcollin.inventorymanagement.domain.storage;


import com.github.javafaker.Faker;

public class ImageFactoryForTests {

    private static final Faker FAKER = Faker.instance();

    public static Image newImageDomain() {
        return Image
                .builder()
                .identifier(FAKER.internet().uuid())
                .preSignedUrl(FAKER.internet().url())
                .build();
    }
}
