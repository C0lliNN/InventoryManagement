package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.raphaelcollin.inventorymanagement.domain.ItemFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Nested
    @DisplayName("method: fromDomain(com.raphaelcollin.inventorymanagement.domain.Item)")
    class FromDomainMethod {

        @Test
        @DisplayName("when called, it should convert from domain to dto")
        void whenCalled_shouldConvertFromDomainToDto() {
            final var itemDomain = ItemFactoryForTests.newItemDomain();

            final var expectedDto = createDtoFromDomain(itemDomain);
            final var actualDto = Item.fromDomain(itemDomain);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        private Item createDtoFromDomain(final com.raphaelcollin.inventorymanagement.domain.Item item) {
            return new Item(
                    item.getId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getQuantity()
            );
        }
    }
}