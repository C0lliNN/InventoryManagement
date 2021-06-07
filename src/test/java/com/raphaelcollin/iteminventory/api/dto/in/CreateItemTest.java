package com.raphaelcollin.iteminventory.api.dto.in;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateItemTest {

    @Nested
    @DisplayName("method: toDomain(String)")
    class ToDomainMethod {
        private final String ITEM_ID = UUID.randomUUID().toString();

        @Test
        @DisplayName("when called, then it should convert from CreateItem to Item")
        void whenCalled_shouldConvertFromCreateItemToItem() {
            final CreateItem createItemDto = ItemFactoryForTests.newCreateItemDto();

            final Item expectedItem = createItemFromDto(createItemDto);
            final Item actualItem = createItemDto.toDomain(ITEM_ID);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        private Item createItemFromDto(final CreateItem createItemDto) {
            return Item
                    .builder()
                    .id(ITEM_ID)
                    .title(createItemDto.getTitle())
                    .description(createItemDto.getDescription())
                    .price(createItemDto.getPrice())
                    .quantity(createItemDto.getQuantity())
                    .build();
        }
    }
}