package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateProductTest {

    @Nested
    @DisplayName("method: toDomain(String)")
    class ToDomainMethod {
        private final String PRODUCT_ID = UUID.randomUUID().toString();
        private final Category CATEGORY = CategoryFactoryForTests.newCategoryDomain();

        @Test
        @DisplayName("when called, then it should convert from CreateProduct to Product")
        void whenCalled_shouldConvertFromCreateProductToProduct() {
            final CreateProduct createProductDto = ProductFactoryForTests.newCreateProductDto();

            final Product expectedProduct = createProductFromDto(createProductDto);
            final Product actualProduct = createProductDto.toDomain(PRODUCT_ID, CATEGORY);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }

        private Product createProductFromDto(final CreateProduct createProductDto) {
            return Product
                    .builder()
                    .id(PRODUCT_ID)
                    .sku(createProductDto.getSku())
                    .name(createProductDto.getName())
                    .description(createProductDto.getDescription())
                    .price(createProductDto.getPrice())
                    .quantity(createProductDto.getQuantity())
                    .imageIdentifier(createProductDto.getImageIdentifier())
                    .category(CATEGORY)
                    .build();
        }
    }
}