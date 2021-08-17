package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import com.raphaelcollin.inventorymanagement.domain.exceptions.RequestValidationException;
import com.raphaelcollin.inventorymanagement.domain.storage.Image;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageStorageClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageStorageClient imageStorageClient;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RequestValidator requestValidator;

    @Nested
    @DisplayName("method: findProducts(SearchProducts)")
    class FindProductsMethod {
        private final SearchProducts searchProducts = ProductFactoryForTests.newSearchProductsDto();
        private final ProductQuery productQuery = searchProducts.toDomain();

        private final Product product1 = ProductFactoryForTests.newProductDomain();
        private final Product product2 = ProductFactoryForTests.newProductDomain();
        private final Product product3 = ProductFactoryForTests.newProductDomain();

        private final Image image1 = ImageFactoryForTests.newImageDomain();
        private final Image image2 = ImageFactoryForTests.newImageDomain();
        private final Image image3 = ImageFactoryForTests.newImageDomain();

        @BeforeEach
        void setUp() {
            when(productRepository.findByQuery(productQuery)).thenReturn(Flux.just(product1, product2, product3));
            when(imageStorageClient.generatePreSignedUrlForVisualization(product1.getImageIdentifier())).thenReturn(Mono.just(image1));
            when(imageStorageClient.generatePreSignedUrlForVisualization(product2.getImageIdentifier())).thenReturn(Mono.just(image2));
            when(imageStorageClient.generatePreSignedUrlForVisualization(product3.getImageIdentifier())).thenReturn(Mono.just(image3));
        }

        @AfterEach
        void tearDown() {
            verify(productRepository).findByQuery(productQuery);
            verify(imageStorageClient).generatePreSignedUrlForVisualization(product1.getImageIdentifier());
            verify(imageStorageClient).generatePreSignedUrlForVisualization(product2.getImageIdentifier());
            verify(imageStorageClient).generatePreSignedUrlForVisualization(product3.getImageIdentifier());

            verifyNoMoreInteractions(productRepository, imageStorageClient);
            verifyNoInteractions(idGenerator, requestValidator, categoryRepository);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying repository and convert the result to dto")
        void whenCalled_shouldForwardTheCallToTheUnderlyingRepositoryAndConvertTheResultDto() {
            StepVerifier.create(productService.findProducts(searchProducts))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.from(product1, image1))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.from(product2, image2))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.from(product3, image3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Product product = ProductFactoryForTests.newProductDomain();
        private final Image image = ImageFactoryForTests.newImageDomain();

        @AfterEach
        void tearDown() {
            verify(productRepository).findById(product.getId());

            verifyNoMoreInteractions(productRepository);
            verifyNoInteractions(requestValidator, idGenerator, categoryRepository);
        }

        @Test
        @DisplayName("when product is not found, then it should return an error")
        void whenProductIsNotFound_shouldReturnAnError() {
            when(productRepository.findById(product.getId())).thenReturn(Mono.empty());

            StepVerifier.create(productService.findById(product.getId()))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Product with ID %s was not found", product.getId())
                    );

            verifyNoInteractions(imageStorageClient);
        }

        @Test
        @DisplayName("when product is found, then it should convert it to dto")
        void whenProductIsFound_shouldConvertItToDto() {
            when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));
            when(imageStorageClient.generatePreSignedUrlForVisualization(product.getImageIdentifier())).thenReturn(Mono.just(image));

            StepVerifier.create(productService.findById(product.getId()))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.from(product, image))
                    .verifyComplete();

            verify(imageStorageClient).generatePreSignedUrlForVisualization(product.getImageIdentifier());
            verifyNoMoreInteractions(imageStorageClient);
        }
    }

    @Nested
    @DisplayName("method: save(CreateProduct)")
    class SaveMethod {
        private final CreateProduct createProduct = ProductFactoryForTests.newCreateProductDto();
        private final Category category = CategoryFactoryForTests.newCategoryDomain();
        private final Product product = createProduct.toDomain(UUID.randomUUID().toString(), category);
        private final Image image = ImageFactoryForTests.newImageDomain();

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(createProduct);
        }

        @Test
        @DisplayName("when validation fails, then it should return an error")
        void whenValidationFails_shouldReturnAnError() {
            when(requestValidator.validate(createProduct)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(productService.save(createProduct))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class)
                            .hasMessage("[]"))
                    .verify();

            verifyNoMoreInteractions(requestValidator);
            verifyNoInteractions(idGenerator, categoryRepository, productRepository, imageStorageClient);
        }

        @Test
        @DisplayName("when category is not found, then it should return an error")
        void whenCategoryIsNotFound_shouldReturnAnError() {
            when(requestValidator.validate(createProduct)).thenReturn(Mono.just(createProduct));
            when(categoryRepository.findById(createProduct.getCategoryId())).thenReturn(Mono.empty());

            StepVerifier.create(productService.save(createProduct))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Category with ID %s was not found", createProduct.getCategoryId())
                    );

            verify(categoryRepository).findById(createProduct.getCategoryId());
            verifyNoInteractions(productRepository, idGenerator, imageStorageClient);
        }

        @Test
        @DisplayName("when the product is created successfully, then it should return the dto representation of it")
        void whenTheProductIsCreatedSuccessfully_shouldReturnTheDtoRepresentationOfIt() {
            when(requestValidator.validate(createProduct)).thenReturn(Mono.just(createProduct));
            when(categoryRepository.findById(createProduct.getCategoryId())).thenReturn(Mono.just(category));
            when(productRepository.save(product)).thenReturn(Mono.empty());
            when(imageStorageClient.generatePreSignedUrlForVisualization(product.getImageIdentifier())).thenReturn(Mono.just(image));
            when(idGenerator.newId()).thenReturn(product.getId());

            StepVerifier.create(productService.save(createProduct))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.from(product, image))
                    .verifyComplete();

            verify(productRepository).save(product);
            verify(categoryRepository).findById(createProduct.getCategoryId());
            verify(idGenerator).newId();
            verify(imageStorageClient).generatePreSignedUrlForVisualization(product.getImageIdentifier());
            verifyNoMoreInteractions(requestValidator, idGenerator, productRepository, categoryRepository);
        }
    }

    @Nested
    @DisplayName("method: updateById(String, UpdateProduct)")
    class UpdateByIdMethod {
        private final Product existingProduct = ProductFactoryForTests.newProductDomain();
        private final Category existingCategory = CategoryFactoryForTests.newCategoryDomain();

        @AfterEach
        void tearDown() {
            verifyNoInteractions(idGenerator, imageStorageClient);
        }

        @Test
        @DisplayName("when validator fails, then it should return an error")
        void whenValidatorFails_shouldReturnAnError() {
            final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDto();
            when(requestValidator.validate(updateProduct)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(productService.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class).hasMessage("[]"))
                    .verify();

            verify(requestValidator).validate(updateProduct);
            verifyNoMoreInteractions(requestValidator);
            verifyNoInteractions(productRepository);
        }

        @Test
        @DisplayName("when product is not found, then it should return an error")
        void whenProductIsNotFound_shouldReturnAnError() {
            final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDto();
            when(requestValidator.validate(updateProduct)).thenReturn(Mono.just(updateProduct));
            when(productRepository.findById(existingProduct.getId())).thenReturn(Mono.empty());

            StepVerifier.create(productService.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Product with ID %s was not found", existingProduct.getId()))
                    .verify();


            verify(requestValidator).validate(updateProduct);
            verify(productRepository).findById(existingProduct.getId());
            verifyNoMoreInteractions(productRepository, requestValidator);
            verifyNoInteractions(categoryRepository);
        }

        @Test
        @DisplayName("when categoryId is present, and category is not found, then it should return an error")
        void whenCategoryIdIsPresent_andCategoryIsNotFound_shouldReturnAnError() {
            final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDto();
            final String categoryId = updateProduct.getCategoryId().orElseThrow();

            when(requestValidator.validate(updateProduct)).thenReturn(Mono.just(updateProduct));
            when(productRepository.findById(existingProduct.getId())).thenReturn(Mono.just(existingProduct));
            when(categoryRepository.findById(categoryId)).thenReturn(Mono.empty());

            StepVerifier.create(productService.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Category with ID %s was not found", categoryId)
                    );

            verify(requestValidator).validate(updateProduct);
            verify(productRepository).findById(existingProduct.getId());
            verify(categoryRepository).findById(categoryId);

            verifyNoMoreInteractions(productRepository, categoryRepository, requestValidator);
        }

        @Test
        @DisplayName("when the categoryId is present, then it should fetch the category and update the product")
        void whenTheCategoryIdIsPresent_shouldFetchTheCategoryAndUpdateTheProduct() {
            final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDto();
            final Product newProduct = updateProduct.toDomain(existingProduct).withCategory(existingCategory);

            when(requestValidator.validate(updateProduct)).thenReturn(Mono.just(updateProduct));
            when(productRepository.findById(existingProduct.getId())).thenReturn(Mono.just(existingProduct));
            when(categoryRepository.findById(updateProduct.getCategoryId().orElseThrow())).thenReturn(Mono.just(existingCategory));
            when(productRepository.save(newProduct)).thenReturn(Mono.empty());

            StepVerifier.create(productService.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .verifyComplete();

            verify(requestValidator).validate(updateProduct);
            verify(productRepository).findById(existingProduct.getId());
            verify(productRepository).save(newProduct);
            verify(categoryRepository).findById(updateProduct.getCategoryId().orElseThrow());
            verifyNoMoreInteractions(requestValidator, productRepository, categoryRepository);
        }

        @Test
        @DisplayName("when the categoryId is not present, then it should not fetch the category")
        void whenTheCategoryIdIsNotPresent_shouldNotFetchTheCategory() {
            final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDtoWithoutCategoryId();
            final Product newProduct = updateProduct.toDomain(existingProduct);

            when(requestValidator.validate(updateProduct)).thenReturn(Mono.just(updateProduct));
            when(productRepository.findById(existingProduct.getId())).thenReturn(Mono.just(existingProduct));
            when(productRepository.save(newProduct)).thenReturn(Mono.empty());

            StepVerifier.create(productService.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .verifyComplete();

            verify(requestValidator).validate(updateProduct);
            verify(productRepository).findById(existingProduct.getId());
            verify(productRepository).save(newProduct);
            verifyNoMoreInteractions(productRepository, requestValidator);
            verifyNoInteractions(categoryRepository);
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final String productId = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(productRepository).deleteById(productId);
            verifyNoInteractions(idGenerator, requestValidator, imageStorageClient, categoryRepository);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("when the delete fails, then it should return an error")
        void whenTheDeleteFails_shouldReturnAnError() {
            when(productRepository.deleteById(productId)).thenReturn(Mono.just(false));

            StepVerifier.create(productService.deleteById(productId))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Product with ID %s was not found", productId)
                    );
        }

        @Test
        @DisplayName("when the delete succeed, then it should return an empty Mono")
        void whenTheDeleteSucceed_shouldReturnAnEmptyMono() {
            when(productRepository.deleteById(productId)).thenReturn(Mono.just(true));

            StepVerifier.create(productService.deleteById(productId))
                    .expectSubscription()
                    .verifyComplete();
        }
    }
}