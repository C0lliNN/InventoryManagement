package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class SearchProductsTest {

    @Nested
    @DisplayName("method: toDomain()")
    class ToDomainMethod {

        @Test
        @DisplayName("when called, then it should return the correct domain object")
        void whenCalled_shouldReturnTheCorrectDomainObject() {
            final SearchProducts searchProducts = ProductFactoryForTests.newSearchProductsDto();

            final ProductQuery expectedQuery = ProductQuery
                    .builder()
                    .name(searchProducts.getName())
                    .minQuantity(searchProducts.getMinQuantity())
                    .categoryId(searchProducts.getCategoryId())
                    .build();

            final ProductQuery actualQuery = searchProducts.toDomain();

            assertThat(actualQuery).isEqualTo(expectedQuery);
        }
    }

    @Nested
    @DisplayName("method: fromQueryParams(MultiValueMap<String, String>)")
    class FromQueryParamsMethod {

        @Test
        @DisplayName("when called with empty map, then it should create a dto with null fields")
        void whenCalledWithEmptyMap_shouldCreateADtoWithNullFields() {
            final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

            final SearchProducts searchProducts = SearchProducts.fromQueryParams(multiValueMap);

            assertThat(searchProducts.getName()).isNull();
            assertThat(searchProducts.getMinQuantity()).isNull();
            assertThat(searchProducts.getCategoryId()).isNull();
        }

        @Test
        @DisplayName("when map contains a name, then it should create a dto with it")
        void whenMapContainsAName_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("name", "Laptop");

            final SearchProducts searchProducts = SearchProducts.fromQueryParams(multiValueMap);

            assertThat(searchProducts.getName()).isEqualTo("Laptop");
            assertThat(searchProducts.getMinQuantity()).isNull();
            assertThat(searchProducts.getCategoryId()).isNull();
        }

        @Test
        @DisplayName("when map contains minQuantity, then it should create a dto with it")
        void whenMapContainsMinQuantity_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("minQuantity", "12");

            final SearchProducts searchProducts = SearchProducts.fromQueryParams(multiValueMap);

            assertThat(searchProducts.getName()).isNull();
            assertThat(searchProducts.getMinQuantity()).isEqualTo(12);
            assertThat(searchProducts.getCategoryId()).isNull();
        }

        @Test
        @DisplayName("when map contains categoryId, then it should create a dto with it")
        void whenMapContainsCategoryId_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("categoryId", "some-id");

            final SearchProducts searchProducts = SearchProducts.fromQueryParams(multiValueMap);

            assertThat(searchProducts.getName()).isNull();
            assertThat(searchProducts.getMinQuantity()).isNull();
            assertThat(searchProducts.getCategoryId()).isEqualTo("some-id");
        }

        @Test
        @DisplayName("when map contains all fields, then it should create a dto with them all")
        void whenMapContainsAllFields_shouldCreateADtoWithThemAll() {
            final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("name", "Notebook");
            multiValueMap.add("minQuantity", "12");
            multiValueMap.add("categoryId", "some-id");

            final SearchProducts searchProducts = SearchProducts.fromQueryParams(multiValueMap);

            assertThat(searchProducts.getName()).isEqualTo("Notebook");
            assertThat(searchProducts.getMinQuantity()).isEqualTo(12);
            assertThat(searchProducts.getCategoryId()).isEqualTo("some-id");
        }
    }
}