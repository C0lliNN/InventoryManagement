package com.raphaelcollin.iteminventory.domain;

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
}
