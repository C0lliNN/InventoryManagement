package com.raphaelcollin.inventorymanagement.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductQueryTest {

    private static final ProductQuery EMPTY_QUERY = ProductQuery.builder().build();

    @Nested
    @DisplayName("method: getName()")
    class GetNameMethod {

        @Test
        @DisplayName("when called and name is null, then it should return Optional empty")
        void whenCalledAndNameIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getName()).isEmpty();
        }

        @Test
        @DisplayName("when called and name is not null, then it should return it wrapped in an Optional")
        void whenCalledAndNameIsNotNull_shouldReturnItWrappedInAnOptional() {
            final String name = UUID.randomUUID().toString();
            final ProductQuery query = ProductQuery.builder().name(name).build();

            assertThat(query.getName()).hasValue(name);
        }
    }

    @Nested
    @DisplayName("method: getMinQuantity()")
    class GetMinQuantityMethod {

        @Test
        @DisplayName("when called and minQuantity is null, then it should return Optional empty")
        void whenCalledAndMinQuantityIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getMinQuantity()).isEmpty();
        }

        @Test
        @DisplayName("when called and name is not null, then it should return it wrapped in an Optional")
        void whenCalledAndNameIsNotNull_shouldReturnItWrappedInAnOptional() {
            final ProductQuery query = ProductQuery.builder().minQuantity(23).build();

            assertThat(query.getMinQuantity()).hasValue(23);
        }
    }

    @Nested
    @DisplayName("method: getCategoryId()")
    class GetCategoryIdMethod {

        @Test
        @DisplayName("when called and categoryId is null, then it should return Optional empty")
        void whenCalledAndCategoryIdIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getCategoryId()).isEmpty();
        }

        @Test
        @DisplayName("when called and categoryId is not null, then it should return it wrapped in an Optional")
        void whenCalledAndCategoryIdIsNotNull_shouldReturnItWrappedInAnOptional() {
            final String categoryId = UUID.randomUUID().toString();
            final ProductQuery query = ProductQuery.builder().categoryId(categoryId).build();

            assertThat(query.getCategoryId()).hasValue(categoryId);
        }
    }
}