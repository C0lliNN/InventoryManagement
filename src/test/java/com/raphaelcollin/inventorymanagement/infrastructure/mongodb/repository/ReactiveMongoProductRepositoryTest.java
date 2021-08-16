package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.DatabaseTestAutoConfiguration;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.ProductSerializer;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


@SpringBootTest(
        classes = {
                DatabaseTestAutoConfiguration.class,
                ReactiveMongoProductRepository.class,
                ProductSerializer.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("com.raphaelcollin.inventorymanagement.infrastructure")
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
@ExtendWith(DatabaseRollbackExtension.class)
class ReactiveMongoProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ReactiveMongoTemplate template;

    @Nested
    @DisplayName("method: findByQuery()")
    class FindByQueryMethod {
        private Product product1;
        private Product product2;
        private Product product3;

        @BeforeEach
        void setUp() {
            this.product1 = ProductFactoryForTests.newProductDomain()
                    .toBuilder()
                    .quantity(5)
                    .name("456")
                    .build();

            this.product2 = ProductFactoryForTests.newProductDomain()
                    .toBuilder()
                    .quantity(10)
                    .name("123")
                    .build();

            this.product3 = ProductFactoryForTests.newProductDomain()
                    .toBuilder()
                    .quantity(15)
                    .name("658")
                    .build();

            Flux.just(product1, product2, product3)
                    .flatMap(repository::save)
                    .blockLast();
        }

        @Test
        @DisplayName("when query is empty, then it should return all persisted products sorted by name")
        void whenQueryIsEmpty_shouldReturnAllPersistedProductsSortedByName() {
            final ProductQuery productQuery = ProductQuery.builder().build();
            final Flux<Product> products = repository.findByQuery(productQuery);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product2, product1, product3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains 'name', then it should return all products matching it")
        void whenQueryContainsName_shouldReturnAllProductsMatchingIt() {
            final ProductQuery query = ProductQuery.builder().name(product1.getName()).build();
            final Flux<Product> products = repository.findByQuery(query);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains 'minQuantity', then it should return all the products which have a quantity greater than or equals to it")
        void whenQueryContainsMinQuantity_shouldReturnAllTheProductsWhichHaveAPriceGreaterThanOrEqualsToIt() {
            final ProductQuery query = ProductQuery.builder().minQuantity(10).build();
            final Flux<Product> products = repository.findByQuery(query);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product2)
                    .expectNext(product3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains 'categoryId', then it should return all the products matching it")
        void whenQueryContainsCategoryId_shouldReturnAllTheProductsMatchingIt() {
            final ProductQuery query = ProductQuery.builder().categoryId(product1.getCategory().getId()).build();
            final Flux<Product> products = repository.findByQuery(query);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains multiple elements, then it should return only the product matching it")
        void whenQueryContainsMultipleElements_shouldReturnOnlyTheProductMatchingIt() {
            final ProductQuery query = ProductQuery.builder()
                    .minQuantity(10)
                    .name(product1.getName())
                    .minQuantity(product1.getQuantity())
                    .build();
            final Flux<Product> products = repository.findByQuery(query);

            StepVerifier.create(products)
                    .expectSubscription()
                    .expectNext(product1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Product product = ProductFactoryForTests.newProductDomain();

        @BeforeEach
        void setUp() {
            repository.save(product).block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return the matching product wrapped into a Mono")
        void whenCalledWithExistingId_shouldReturnTheMatchingProductWrappedIntoAMono() {
            final Mono<Product> foundProduct = repository.findById(product.getId());

            StepVerifier.create(foundProduct)
                    .expectSubscription()
                    .expectNext(product)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            final Mono<Product> foundProduct = repository.findById(UUID.randomUUID().toString());

            StepVerifier.create(foundProduct)
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("class: save(Product)")
    class SaveMethod {

        @Test
        @DisplayName("when called and the product was not previously saved, then it should persist the product")
        void whenCalledAndTheProductsWasNotPreviouslySaved_shouldPersistTheProduct() {
            final Product product = ProductFactoryForTests.newProductDomain();

            final Mono<Product> mono = repository.save(product)
                    .then(repository.findById(product.getId()));

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(product)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called and the product was previously saved, then it should update the product")
        void whenTheFieldIdMatchesAnExistingProduct_shouldThrowAnException() {
            final Product persistedTime = ProductFactoryForTests.newProductDomain();
            final Product newProduct = persistedTime.toBuilder().name("newName").build();

            final Mono<Product> mono = repository.save(persistedTime)
                    .then(repository.save(newProduct))
                    .then(repository.findById(persistedTime.getId()));

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(newProduct)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final Product product = ProductFactoryForTests.newProductDomain();

        @BeforeEach
        void setUp() {
            repository.save(product).block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return true")
        void whenCalledWithExistingId_shouldReturnTrue() {
            Mono<Boolean> mono = repository.deleteById(product.getId());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return false")
        void whenCalledWithUnknown_shouldReturnFalse() {
            Mono<Boolean> mono = repository.deleteById(UUID.randomUUID().toString());

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(ProductDocument.class).block();
    }
}