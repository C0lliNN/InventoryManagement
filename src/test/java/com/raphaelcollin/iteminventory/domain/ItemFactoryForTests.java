package com.raphaelcollin.iteminventory.domain;

import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemFactoryForTests {

    public static Item newItemDomain() {
        return Item
                .builder()
                .id(UUID.randomUUID().toString())
                .title(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .price(BigDecimal.valueOf(23))
                .quantity(2)
                .build();
    }

    public static ItemDocument newItemDocument() {
        return new ItemDocument(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(23),
                2
        );
    }
}
