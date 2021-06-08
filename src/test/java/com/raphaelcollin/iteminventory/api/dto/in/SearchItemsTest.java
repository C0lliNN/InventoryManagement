package com.raphaelcollin.iteminventory.api.dto.in;

import com.raphaelcollin.iteminventory.domain.ItemQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SearchItemsTest {

    @Nested
    @DisplayName("method: toDomain()")
    class ToDomainMethod {

        @ParameterizedTest
        @CsvSource({
                "title1,3,title1,3",
                "title1,0,title1,0",
                "null,3,null,3",
                "null,null,null,null",
        })
        @DisplayName("when called, then it should return the correct domain object")
        void whenCalled_shouldReturnTheCorrectDomainObject(
                String actualTitle,
                int actualMinQuantity,
                String expectedTitle,
                int expectedMinQuantity) {

            final ItemQuery expectedQuery = ItemQuery
                    .builder()
                    .title(expectedTitle)
                    .minQuantity(expectedMinQuantity)
                    .build();

            final SearchItems searchItems = new SearchItems(actualTitle, actualMinQuantity);
            final ItemQuery actualQuery = searchItems.toDomain();

            Assertions.assertThat(actualQuery).isEqualTo(expectedQuery);
        }
    }
}