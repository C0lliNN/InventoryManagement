package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SearchProductsTest {

    @Nested
    @DisplayName("method: toDomain()")
    class ToDomainMethod {

        @ParameterizedTest
        @CsvSource({
                "title1,3,title1,3",
                "title1,0,title1,0",
                "null,3,null,3",
                "null,0,null,0",
        })
        @DisplayName("when called, then it should return the correct domain object")
        void whenCalled_shouldReturnTheCorrectDomainObject(
                String actualTitle,
                int actualMinQuantity,
                String expectedTitle,
                int expectedMinQuantity) {

            final ProductQuery expectedQuery = ProductQuery
                    .builder()
                    .title(expectedTitle)
                    .minQuantity(expectedMinQuantity)
                    .build();

            final SearchProducts searchProducts = new SearchProducts(actualTitle, actualMinQuantity);
            final ProductQuery actualQuery = searchProducts.toDomain();

            Assertions.assertThat(actualQuery).isEqualTo(expectedQuery);
        }
    }
}