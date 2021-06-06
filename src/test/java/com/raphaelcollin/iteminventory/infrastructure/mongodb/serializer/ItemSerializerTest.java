package com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSerializerTest {
    private static final ItemSerializer SERIALIZER = new ItemSerializer();

    @Nested
    @DisplayName("method: fromDocument(ItemDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from ItemDocument to Item")
        void whenCalled_shouldConvertFromItemDocumentToItem() {
            final ItemDocument document = ItemFactoryForTests.newItemDocument();

            final Item expectedItem = createDomainFromDocument(document);
            final Item actualItem = SERIALIZER.fromDocument(document);

            assertThat(actualItem).isEqualTo(expectedItem);
        }

        private Item createDomainFromDocument(final ItemDocument document) {
            return Item
                    .builder()
                    .id(document.getId())
                    .title(document.getTitle())
                    .description(document.getDescription())
                    .price(document.getPrice())
                    .quantity(document.getQuantity())
                    .build();
        }
    }

    @Nested
    @DisplayName("method: toDocument()")
    class ToDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from Item to ItemDocument")
        void whenCalled_shouldConvertFromItemToItemDocument() {
            final Item domain = ItemFactoryForTests.newItemDomain();

            final ItemDocument expectedDocument = createDocumentFromDomain(domain);
            final ItemDocument actualDocument = SERIALIZER.toDocument(domain);

            assertThat(actualDocument).isEqualTo(expectedDocument);
        }

        private ItemDocument createDocumentFromDomain(final Item domain) {
            return new ItemDocument(
                    domain.getId(),
                    domain.getTitle(),
                    domain.getDescription(),
                    domain.getPrice(),
                    domain.getQuantity()
            );
        }
    }
}