package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import com.raphaelcollin.inventorymanagement.domain.ProductService;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.RequestValidationException;
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
class ProductApiTest {

    @InjectMocks
    private ProductApi productApi;

    @Mock
    private ProductService productService;

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

        @BeforeEach
        void setUp() {
            when(productService.findByQuery(productQuery)).thenReturn(Flux.just(product1, product2, product3));
        }

        @AfterEach
        void tearDown() {
            verify(productService).findByQuery(productQuery);
            verifyNoMoreInteractions(productService);

            verifyNoInteractions(idGenerator, requestValidator);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service and convert the result to dto")
        void whenCalled_shouldForwardTheCallToTheUnderlyingServiceAndConvertTheResultDto() {
            StepVerifier.create(productApi.findProducts(searchProducts))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.fromDomain(product1))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.fromDomain(product2))
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.fromDomain(product3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Product product1 = ProductFactoryForTests.newProductDomain();

        @BeforeEach
        void setUp() {
            when(productService.findById(product1.getId())).thenReturn(Mono.just(product1));
        }

        @AfterEach
        void tearDown() {
            verify(productService).findById(product1.getId());

            verifyNoMoreInteractions(productService);
            verifyNoInteractions(requestValidator, idGenerator);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingServiceAndConvertTheResultDto() {
            StepVerifier.create(productApi.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.fromDomain(product1))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: save(CreateProduct)")
    class SaveMethod {
        private final CreateProduct createProduct = ProductFactoryForTests.newCreateProductDto();
        private Product product;

        @BeforeEach
        void setUp() {
            final String productId = UUID.randomUUID().toString();
            this.product = createProduct.toDomain(productId);
        }

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(createProduct);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingService() {
            when(requestValidator.validate(createProduct)).thenReturn(Mono.just(createProduct));
            when(productService.save(product)).thenReturn(Mono.just(product));
            when(idGenerator.newId()).thenReturn(product.getId());

            StepVerifier.create(productApi.save(createProduct))
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.inventorymanagement.api.dto.out.Product.fromDomain(product))
                    .verifyComplete();

            verify(productService).save(product);
            verify(idGenerator).newId();
            verifyNoMoreInteractions(requestValidator, idGenerator, productService);
        }

        @Test
        @DisplayName("when validation fails, then it should throw an error")
        void whenValidationFails_shouldThrowAnError() {
            when(requestValidator.validate(createProduct)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(productApi.save(createProduct))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class)
                            .hasMessage("[]"))
                    .verify();

            verifyNoMoreInteractions(requestValidator);
            verifyNoInteractions(idGenerator, productService);
        }
    }

    @Nested
    @DisplayName("method: updateById(String, UpdateProduct)")
    class UpdateByIdMethod {
        private final UpdateProduct updateProduct = ProductFactoryForTests.newUpdateProductDto();
        private final Product existingProduct = ProductFactoryForTests.newProductDomain();
        private final Product newProduct = updateProduct.toDomain(existingProduct);

        @AfterEach
        void tearDown() {
            verify(requestValidator).validate(updateProduct);
            verifyNoInteractions(idGenerator);
            verifyNoMoreInteractions(requestValidator);
        }

        @Test
        @DisplayName("when called, then it should forward the calls to the underlying services")
        void whenCalled_shouldForwardTheCallsToTheUnderlyingServices() {
            when(requestValidator.validate(updateProduct)).thenReturn(Mono.just(updateProduct));
            when(productService.findById(existingProduct.getId())).thenReturn(Mono.just(existingProduct));
            when(productService.save(newProduct)).thenReturn(Mono.just(newProduct));

            StepVerifier.create(productApi.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .verifyComplete();

            verify(productService).findById(existingProduct.getId());
            verify(productService).save(newProduct);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("when validator fails, then it should throw an error")
        void whenValidatorFails_shouldThrowAnError() {
            when(requestValidator.validate(updateProduct)).thenReturn(Mono.error(new RequestValidationException(emptyList())));

            StepVerifier.create(productApi.updateById(existingProduct.getId(), updateProduct))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class).hasMessage("[]"))
                    .verify();

            verifyNoInteractions(productService);
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final String productId = UUID.randomUUID().toString();

        @BeforeEach
        void setUp() {
            when(productService.deleteById(productId)).thenReturn(Mono.empty());
        }

        @AfterEach
        void tearDown() {
            verify(productService).deleteById(productId);
            verifyNoInteractions(idGenerator, requestValidator);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying service")
        void whenCalled_shouldForwardTheCallToTheUnderlyingService() {
            productApi.deleteById(productId).block();
        }
    }
}