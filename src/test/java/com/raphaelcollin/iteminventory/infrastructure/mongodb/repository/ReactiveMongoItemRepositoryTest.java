package com.raphaelcollin.iteminventory.infrastructure.mongodb.repository;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import com.raphaelcollin.iteminventory.domain.ItemRepository;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.iteminventory.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


@SpringBootTest
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
class ReactiveMongoItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private ReactiveMongoTemplate template;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findAllItems()")
    class FindAllItemsMethod {
        private final Item item1 = ItemFactoryForTests.newItemDomain();
        private final Item item2 = ItemFactoryForTests.newItemDomain();
        private final Item item3 = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            Flux.just(item1, item2, item3)
                    .flatMap(repository::save)
                    .log()
                    .blockLast();
        }

        @Test
        @DisplayName("when called, then it should return all persisted items")
        void whenCalled_shouldReturnAllPersistedItems() {
            final Flux<Item> items = repository.findAllItems().log();

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNextCount(3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findItemById(String)")
    class FindItemByIdMethod {
        private final Item item = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            repository.save(item)
                    .block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return the matching item wrapped into a Mono")
        void whenCalledWithExistingId_shouldReturnTheMatchingItemWrappedIntoAMono() {
            final Mono<Item> foundItem = repository.findItemById(item.getId());

            StepVerifier.create(foundItem)
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            final Mono<Item> foundItem = repository.findItemById(UUID.randomUUID().toString());

            StepVerifier.create(foundItem)
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("class: save(Item)")
    class SaveMethod {

        @Test
        @DisplayName("when called, then it should persist the item")
        void whenCalled_shouldPersistTheItem() {
            final Item item = ItemFactoryForTests.newItemDomain();

            final Mono<Item> mono = repository.save(item)
                    .then(repository.findItemById(item.getId()));

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(ItemDocument.class).block();
    }
}