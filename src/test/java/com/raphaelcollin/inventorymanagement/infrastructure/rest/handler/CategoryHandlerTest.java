package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateCategory;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.infrastructure.DatabaseTestAutoConfiguration;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository.ReactiveMongoCategoryRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.ProductSerializer;
import com.raphaelcollin.inventorymanagement.utils.extensions.DatabaseRollbackExtension;
import com.raphaelcollin.inventorymanagement.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ContextConfiguration(initializers = {
        DatabaseContainerInitializer.class
})
@SpringBootTest(classes = {
        ReactiveMongoCategoryRepository.class,
        DatabaseTestAutoConfiguration.class,
        ProductSerializer.class
})
@AutoConfigureWebTestClient
@ComponentScan("com.raphaelcollin.inventorymanagement")
@ExtendWith(DatabaseRollbackExtension.class)
class CategoryHandlerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private IdGenerator idGenerator;

    private static final String BASE_URL = "/api/v1/categories";

    @Nested
    @DisplayName("method: findAllCategories(ServerRequest)")
    class FindAllCategoriesMethod {
        private Category category1;
        private Category category2;
        private Category category3;

        @BeforeEach
        void setUp() {
            this.category1 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 2")
                    .build();

            this.category2 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 1")
                    .build();
            this.category3 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 3")
                    .build();

            categoryRepository.save(category1).block();
            categoryRepository.save(category2).block();
            categoryRepository.save(category3).block();
        }

        @Test
        @DisplayName("when called, then it should return all the categories ordered by name")
        void whenCalled_shouldReturnAllTheCategoriesOrderedByName() {
            client.get()
                    .uri(BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(category2.getId()))
                    .jsonPath("$[0].name").value(is(category2.getName()))
                    .jsonPath("$[1].id").value(is(category1.getId()))
                    .jsonPath("$[1].name").value(is(category1.getName()))
                    .jsonPath("$[2].id").value(is(category3.getId()))
                    .jsonPath("$[2].name").value(is(category3.getName()));
        }
    }

    @Nested
    @DisplayName("method: saveCategory(ServerRequest)")
    class SaveCategoryMethod {
        private final String CATEGORY_ID = UUID.randomUUID().toString();

        @Test
        @DisplayName("when called with valid payload, then it should return 201")
        void whenCalledWithValidPayload_shouldReturn201() {
            when(idGenerator.newId()).thenReturn(CATEGORY_ID);

            final CreateCategory createCategory = new CreateCategory("Category name");

            client.post()
                    .uri(BASE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createCategory)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").value(is(CATEGORY_ID))
                    .jsonPath("$.name").value(is("Category name"));
        }

        @Test
        @DisplayName("when called without name, then it should return 400 error")
        void whenCalledWithoutName_shouldReturn400Error() {
            final CreateCategory createCategory = new CreateCategory(null);

            client.post()
                    .uri(BASE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createCategory)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid name, then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final CreateCategory createCategory = new CreateCategory(UUID.randomUUID().toString().repeat(5));

            client.post()
                    .uri(BASE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createCategory)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }
    }

    @Nested
    @DisplayName("method: updateCategoryById(ServerRequest)")
    class UpdateCategoryByIdMethod {
        private final Category category = CategoryFactoryForTests.newCategoryDomain();

        @BeforeEach
        void setUp() {
            categoryRepository.save(category).block();
        }

        @Test
        @DisplayName("when called with invalid name, then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final UpdateCategory updateCategory = new UpdateCategory(UUID.randomUUID().toString().repeat(5));

            client.patch()
                    .uri(BASE_URL + "/{categoryId}", category.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateCategory)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with unknown categoryId, then it should return 404 error")
        void whenCalledWithUnknownCategoryId_shouldReturn404() {
            final UpdateCategory updateCategory = CategoryFactoryForTests.newUpdateCategory();
            final String unknownCategoryId = UUID.randomUUID().toString();

            client.patch()
                    .uri(BASE_URL + "/{categoryId}", unknownCategoryId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateCategory)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is(format("Category with ID %s was not found", unknownCategoryId)))
                    .jsonPath("$.details").isEmpty();
        }

        @Test
        @DisplayName("when called with valid payload and existing categoryId, then it should return 204")
        void whenCalledWithValidPayloadAndExistingCategoryId_shouldReturn204() {
            final String name = "New Category Name";
            final UpdateCategory updateCategory = new UpdateCategory(name);

            client.patch()
                    .uri(BASE_URL + "/{categoryId}", category.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateCategory)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final var expected = category.toBuilder().name(name).build();
            final var actual = categoryRepository.findById(category.getId()).block();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("method: deleteCategoryById(ServerRequest)")
    class DeleteCategoryByIdMethod {
        private final Category category = CategoryFactoryForTests.newCategoryDomain();

        @BeforeEach
        void setUp() {
            categoryRepository.save(category).block();
        }

        @Test
        @DisplayName("when called with unknown categoryId, then it should return 404 error")
        void whenCalledWithUnknownCategoryId_shouldReturn404() {
            final String unknownCategoryId = UUID.randomUUID().toString();

            client.delete()
                    .uri(BASE_URL + "/{categoryId}", unknownCategoryId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is(format("Category with ID %s was not found", unknownCategoryId)))
                    .jsonPath("$.details").isEmpty();

            assertThat(categoryRepository.findById(category.getId()).blockOptional()).hasValue(category);
        }

        @Test
        @DisplayName("when called with existing id, then it should return 204")
        void whenCalledWithExistingId_shouldReturn204() {
            client.delete()
                    .uri(BASE_URL + "/{categoryId}", category.getId())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            assertThat(categoryRepository.findById(category.getId()).blockOptional()).isEmpty();
        }
    }
}