package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySerializerTest {

    private final CategorySerializer serializer = new CategorySerializer();

    @Nested
    @DisplayName("method: fromDocument(CategoryDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from CategoryDocument to domain")
        void whenCalled_shouldConvertFromCategoryDocumentToDomain() {
            final CategoryDocument document = CategoryFactoryForTests.newCategoryDocument();

            final Category expected = Category
                    .builder()
                    .id(document.getId())
                    .name(document.getName())
                    .build();

            final Category actual = serializer.fromDocument(document);

            assertThat(expected).isEqualTo(actual);
        }
    }

    @Nested
    @DisplayName("method: toDocument(Category)")
    class ToDocumentMethod {

        @Test
        @DisplayName("when called, then it should convert from Category to document")
        void whenCalled_shouldConvertFromCategoryToDocument() {
            final Category category = CategoryFactoryForTests.newCategoryDomain();

            final CategoryDocument expected = new CategoryDocument(category.getId(), category.getName());
            final CategoryDocument actual = serializer.toDocument(category);

            assertThat(actual).isEqualTo(expected);
        }
    }

}