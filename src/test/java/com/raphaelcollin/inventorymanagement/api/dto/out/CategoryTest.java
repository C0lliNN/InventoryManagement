package com.raphaelcollin.inventorymanagement.api.dto.out;

import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Nested
    @DisplayName("method: fromDomain(Category)")
    class FromDomainMethod {

        @Test
        @DisplayName("when called, then it should convert from domain to dto")
        void whenCalled_shouldConvertFromDomainToDto() {
            final var domain = CategoryFactoryForTests.newCategoryDomain();

            final Category expected = new Category(domain.getId(), domain.getName());
            final Category actual = Category.fromDomain(domain);

            assertThat(actual).isEqualTo(expected);
        }
    }
}