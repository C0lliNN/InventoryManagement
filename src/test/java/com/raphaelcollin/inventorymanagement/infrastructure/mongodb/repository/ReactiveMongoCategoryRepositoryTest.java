package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.DatabaseTestAutoConfiguration;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.CategorySerializer;
import com.raphaelcollin.inventorymanagement.utils.extensions.DatabaseRollbackExtension;
import com.raphaelcollin.inventorymanagement.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

@SpringBootTest(
        classes = {
                DatabaseTestAutoConfiguration.class,
                ReactiveMongoCategoryRepository.class,
                CategorySerializer.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("com.raphaelcollin.inventorymanagement.infrastructure")
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
@ExtendWith(DatabaseRollbackExtension.class)
class ReactiveMongoCategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findAll()")
    class FindAllMethod {
        private Category category1;
        private Category category2;
        private Category category3;

        @BeforeEach
        void setUp() {
            category1 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 2")
                    .build();
            category2 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 1")
                    .build();
            category3 = CategoryFactoryForTests.newCategoryDomain()
                    .toBuilder()
                    .name("Category 3")
                    .build();

            repository.save(category1).block();
            repository.save(category2).block();
            repository.save(category3).block();
        }

        @Test
        @DisplayName("when called, then it should return all categories ordered by name")
        void whenCalled_shouldReturnAllCategoriesOrderedByName() {
            StepVerifier.create(repository.findAll())
                    .expectSubscription()
                    .expectNext(category2, category1, category3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Category category = CategoryFactoryForTests.newCategoryDomain();

        @BeforeEach
        void setUp() {
            repository.save(category).block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return the matching category")
        void whenCalledWithExistingId_shouldReturnTheMatchingCategory() {
            StepVerifier.create(repository.findById(category.getId()))
                    .expectSubscription()
                    .expectNext(category)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            StepVerifier.create(repository.findById("categoryId"))
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: save(Category)")
    class SaveMethod {

        @Test
        @DisplayName("when category was not previously saved, then it should persist it")
        void whenCategoryWasNotPreviouslySaved_shouldPersistIt() {
            final Category category = CategoryFactoryForTests.newCategoryDomain();

            repository.save(category).block();

            StepVerifier.create(repository.findById(category.getId()))
                    .expectSubscription()
                    .expectNext(category)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when category was previously saved, then it should update it")
        void whenCategoryWasPreviouslySaved_shouldUpdateIt() {
            final Category category = CategoryFactoryForTests.newCategoryDomain();
            repository.save(category).block();

            final Category newCategory = category.toBuilder().name("New Category Name").build();
            repository.save(newCategory).block();

            StepVerifier.create(repository.findById(category.getId()))
                    .expectSubscription()
                    .expectNext(newCategory)
                    .verifyComplete();
        }
    }
    
    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteById {
        private final Category category = CategoryFactoryForTests.newCategoryDomain();

        @BeforeEach
        void setUp() {
            repository.save(category).block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return true and delete the category")
        void whenCalledWithExistingId_shouldReturnTrueAndDeleteTheCategory() {
            StepVerifier.create(repository.deleteById(category.getId()))
                    .expectSubscription()
                    .expectNext(true)
                    .verifyComplete();

            StepVerifier.create(repository.findById(category.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return false and no category should be deleted")
        void whenCalledWithUnknownId_shouldReturnFalseAndNoCategoryShouldBeDeleted() {
            StepVerifier.create(repository.deleteById("categoryId"))
                    .expectSubscription()
                    .expectNext(false)
                    .verifyComplete();

            StepVerifier.create(repository.findById(category.getId()))
                    .expectSubscription()
                    .expectNext(category)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        mongoTemplate.dropCollection(CategoryDocument.class).block();
    }
}