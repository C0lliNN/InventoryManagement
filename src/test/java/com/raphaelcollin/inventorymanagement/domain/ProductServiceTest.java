package com.raphaelcollin.inventorymanagement.domain;

import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("method: findByQuery(ProductQuery)")
    class FindByQueryMethod {
        private final ProductQuery productQuery = ProductFactoryForTests.newProductQuery();
        private final Product product1 = ProductFactoryForTests.newProductDomain();
        private final Product product2 = ProductFactoryForTests.newProductDomain();

        @AfterEach
        void tearDown() {
            verify(productRepository).findByQuery(productQuery);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("when called, then it should forward the call to the underlying repository")
        void whenCalled_shouldForwardTheCallToTheUnderlyingRepository() {
            when(productRepository.findByQuery(productQuery)).thenReturn(Flux.just(product1, product2));

            final Flux<Product> products = productService.findByQuery(productQuery);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product1, product2)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Product product = ProductFactoryForTests.newProductDomain();

        @AfterEach
        void tearDown() {
            verify(productRepository).findById(product.getId());
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("when the product is found, then it should return the matching product")
        void whenProductIsFound_shouldReturnTheMatchingProduct() {
            when(productRepository.findById(product.getId())).thenReturn(Mono.just(product));

            final Mono<Product> mono = productService.findById(product.getId());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(product)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when the product is not found, then it should return an error")
        void whenProductIsNotFound_shouldReturnAnError() {
            when(productRepository.findById(product.getId())).thenReturn(Mono.empty());

            final Mono<Product> mono = productService.findById(product.getId());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectError(EntityNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("method: save(Product)")
    class SaveMethod {
        private final Product product = ProductFactoryForTests.newProductDomain();

        @BeforeEach
        void setUp() {
            when(productRepository.save(product)).thenReturn(Mono.empty());
        }

        @AfterEach
        void tearDown() {
            verify(productRepository).save(product);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("when called, then it should forward the call to underlying product")
        void whenCalled_shouldForwardTheCallToUnderlyingProduct() {
            StepVerifier.create(productService.save(product))
                    .expectSubscription()
                    .expectNext(product)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        final String productId = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(productRepository).deleteById(productId);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("when the product is deleted successfully, then it should not return any error")
        void whenProductIsDeletedSuccessfully_shouldNotReturnAnyError() {
            when(productRepository.deleteById(productId)).thenReturn(Mono.just(true));

            Mono<Void> mono = productService.deleteById(productId);

            StepVerifier.create(mono)
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName("when the product is not deleted successfully, then it should return an error")
        void whenTheProductIsNotDeletedSuccessfully_shouldReturnAnError() {
            when(productRepository.deleteById(productId)).thenReturn(Mono.just(false));

            Mono<Void> mono = productService.deleteById(productId);

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectError(EntityNotFoundException.class)
                    .verify();
        }
    }

}