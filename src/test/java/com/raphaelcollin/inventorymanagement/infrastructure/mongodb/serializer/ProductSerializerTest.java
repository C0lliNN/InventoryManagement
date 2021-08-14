package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSerializerTest {
    private static final ProductSerializer SERIALIZER = new ProductSerializer();

    @Nested
    @DisplayName("method: fromDocument(ProductDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from ProductDocument to Product")
        void whenCalled_shouldConvertFromProductDocumentToProduct() {
            final ProductDocument document = ProductFactoryForTests.newProductDocument();

            final Product expectedProduct = createDomainFromDocument(document);
            final Product actualProduct = SERIALIZER.fromDocument(document);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        private Product createDomainFromDocument(final ProductDocument document) {
            return Product
                    .builder()
                    .id(document.getId())
                    .sku(document.getSku())
                    .name(document.getName())
                    .description(document.getDescription())
                    .price(document.getPrice())
                    .quantity(document.getQuantity())
                    .imageIdentifier(document.getImageIdentifier())
                    .build();
        }
    }

    @Nested
    @DisplayName("method: toDocument()")
    class ToDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from Product to ProductDocument")
        void whenCalled_shouldConvertFromProductToProductDocument() {
            final Product domain = ProductFactoryForTests.newProductDomain();

            final ProductDocument expectedDocument = createDocumentFromDomain(domain);
            final ProductDocument actualDocument = SERIALIZER.toDocument(domain);

            assertThat(actualDocument).isEqualTo(expectedDocument);
        }

        private ProductDocument createDocumentFromDomain(final Product domain) {
            return new ProductDocument(
                    domain.getId(),
                    domain.getSku(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getPrice(),
                    domain.getQuantity(),
                    domain.getImageIdentifier()
            );
        }
    }
}