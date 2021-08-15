package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateCategory;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import com.raphaelcollin.inventorymanagement.domain.exceptions.RequestValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RequestValidator requestValidator;

    @Nested
    @DisplayName("method: findAll()")
    class FindAllMethod {
        private final Category category1 = CategoryFactoryForTests.newCategoryDomain();
        private final Category category2 = CategoryFactoryForTests.newCategoryDomain();
        private final Category category3 = CategoryFactoryForTests.newCategoryDomain();

        @AfterEach
        void tearDown() {
            verify(categoryRepository).findAll();
            verifyNoMoreInteractions(categoryRepository);

            verifyNoInteractions(requestValidator, idGenerator);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the repository")
        void whenCalled_shouldForwardTheCallToTheRepository() {
            when(categoryRepository.findAll()).thenReturn(Flux.just(category1, category2, category3));

            StepVerifier.create(categoryService.findAll())
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Category.fromDomain(category1))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Category.fromDomain(category2))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Category.fromDomain(category3))
                    .verifyComplete();
        }
    }
    
    @Nested
    @DisplayName("method: save(CreateCategory)")
    class SaveMethod {
        private final String CATEGORY_ID = UUID.randomUUID().toString();
        private final CreateCategory createCategory = CategoryFactoryForTests.newCreateCategory();

        private final Category category = createCategory.toDomain(CATEGORY_ID);

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(createCategory);
            verifyNoMoreInteractions(requestValidator);
        }

        @Test
        @DisplayName("when validation fails, then it should not forward more calls")
        void whenValidationFails_shouldNotForwardMoreCalls() {
            final var error = new RequestValidationException(Collections.emptyList());

            when(requestValidator.validate(createCategory)).thenReturn(Mono.error(error));

            StepVerifier.create(categoryService.save(createCategory))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);

            verifyNoInteractions(idGenerator, categoryRepository);
        }

        @Test
        @DisplayName("when validation succeed, then it should forward the calls")
        void whenValidationSucceed_shouldForwardTheCalls() {
            when(requestValidator.validate(createCategory)).thenReturn(Mono.just(createCategory));
            when(idGenerator.newId()).thenReturn(CATEGORY_ID);
            when(categoryRepository.save(category)).thenReturn(Mono.empty());

            StepVerifier.create(categoryService.save(createCategory))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Category.fromDomain(category))
                    .verifyComplete();

            verify(idGenerator).newId();
            verify(categoryRepository).save(category);

            verifyNoMoreInteractions(idGenerator, categoryRepository);
        }
    }

    @Nested
    @DisplayName("method: updateById(String, UpdateCategory)")
    class UpdateByIdMethod {
        private final Category existingCategory = CategoryFactoryForTests.newCategoryDomain();
        private final UpdateCategory updateCategory = CategoryFactoryForTests.newUpdateCategory();
        private final Category newCategory = updateCategory.toDomain(existingCategory);

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(updateCategory);
            verifyNoMoreInteractions(requestValidator);

            verifyNoInteractions(idGenerator);
        }

        @Test
        @DisplayName("when validation fails, then it should not forward any more calls")
        void whenValidationFails_shouldNotForwardAnyMoreCalls() {
            final var error = new RequestValidationException(Collections.emptyList());
            when(requestValidator.validate(updateCategory)).thenReturn(Mono.error(error));

            StepVerifier.create(categoryService.updateById(existingCategory.getId(), updateCategory))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);

            verifyNoInteractions(categoryRepository);
        }

        @Test
        @DisplayName("when category is not found, then it should return an error")
        void whenCategoryIsNotFound_shouldReturnAnError() {
            when(requestValidator.validate(updateCategory)).thenReturn(Mono.just(updateCategory));
            when(categoryRepository.findById(existingCategory.getId())).thenReturn(Mono.empty());

            StepVerifier.create(categoryService.updateById(existingCategory.getId(), updateCategory))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Category with ID %s was not found", existingCategory.getId())
                    );

            verify(categoryRepository).findById(existingCategory.getId());
            verifyNoMoreInteractions(categoryRepository);
        }

        @Test
        @DisplayName("when category is found, then it should update it and no errors should happen")
        void whenCategoryIsFound_shouldUpdateItAndNoErrorsShouldHappen() {
            when(requestValidator.validate(updateCategory)).thenReturn(Mono.just(updateCategory));
            when(categoryRepository.findById(existingCategory.getId())).thenReturn(Mono.just(existingCategory));
            when(categoryRepository.save(newCategory)).thenReturn(Mono.empty());

            StepVerifier.create(categoryService.updateById(existingCategory.getId(), updateCategory))
                    .expectSubscription()
                    .verifyComplete();

            verify(categoryRepository).findById(existingCategory.getId());
            verify(categoryRepository).save(newCategory);
            verifyNoMoreInteractions(categoryRepository);
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final String CATEGORY_ID = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(categoryRepository).deleteById(CATEGORY_ID);
            verifyNoMoreInteractions(categoryRepository);

            verifyNoInteractions(idGenerator, requestValidator);
        }

        @Test
        @DisplayName("when category is deleted successfully, then it should not return any error")
        void whenCategoryIsDeletedSuccessfully_shouldNotReturnAnyError() {
            when(categoryRepository.deleteById(CATEGORY_ID)).thenReturn(Mono.just(true));

            StepVerifier.create(categoryService.deleteById(CATEGORY_ID))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName("when category is not deleted successfully, then it should not return an error")
        void whenCategoryIsNotDeletedSuccessfully_shouldNotReturnAnError() {
            when(categoryRepository.deleteById(CATEGORY_ID)).thenReturn(Mono.just(false));

            StepVerifier.create(categoryService.deleteById(CATEGORY_ID))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Category with ID %s was not found", CATEGORY_ID)
                    );
        }
    }
}