package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Nested
    @DisplayName("method: fromDomain(com.raphaelcollin.inventorymanagement.domain.Product)")
    class FromDomainMethod {

        @Test
        @DisplayName("when called, it should convert from domain to dto")
        void whenCalled_shouldConvertFromDomainToDto() {
            final var productDomain = ProductFactoryForTests.newProductDomain();

            final var expectedDto = createDtoFromDomain(productDomain);
            final var actualDto = Product.fromDomain(productDomain);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        private Product createDtoFromDomain(final com.raphaelcollin.inventorymanagement.domain.Product product) {
            return new Product(
                    product.getId(),
                    product.getTitle(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity()
            );
        }
    }
}