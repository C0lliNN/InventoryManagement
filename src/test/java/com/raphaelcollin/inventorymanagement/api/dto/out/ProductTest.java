package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.storage.Image;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Nested
    @DisplayName("method: fromDomain(com.raphaelcollin.inventorymanagement.domain.Product, Image)")
    class FromMethod {

        @Test
        @DisplayName("when called, it should convert from domain to dto")
        void whenCalled_shouldConvertFromDomainToDto() {
            final var productDomain = ProductFactoryForTests.newProductDomain();
            final var imageDomain = ImageFactoryForTests.newImageDomain();

            final var expectedDto = createDtoFromDomain(productDomain, imageDomain);
            final var actualDto = Product.from(productDomain, imageDomain);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        private Product createDtoFromDomain(final com.raphaelcollin.inventorymanagement.domain.Product product,
                                            final Image image) {
            return new Product(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity(),
                    image.getPreSignedUrl()
            );
        }
    }
}