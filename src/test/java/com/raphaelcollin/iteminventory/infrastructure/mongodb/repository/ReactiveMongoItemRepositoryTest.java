package com.raphaelcollin.iteminventory.infrastructure.mongodb.repository;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import com.raphaelcollin.iteminventory.domain.ItemQuery;
import com.raphaelcollin.iteminventory.domain.ItemRepository;
import com.raphaelcollin.iteminventory.infranstructure.config.DatabaseTestAutoConfiguration;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer.ItemSerializer;
import com.raphaelcollin.iteminventory.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


@SpringBootTest(
        classes = {
                DatabaseTestAutoConfiguration.class,
                ReactiveMongoItemRepository.class,
                ItemSerializer.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
@EnableAutoConfiguration
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
    @DisplayName("method: findByQuery()")
    class FindByQueryMethod {
        private Item item1;
        private Item item2;
        private Item item3;

        @BeforeEach
        void setUp() {
            this.item1 = ItemFactoryForTests.newItemDomain()
                    .toBuilder()
                    .quantity(5)
                    .title("456")
                    .build();

            this.item2 = ItemFactoryForTests.newItemDomain()
                    .toBuilder()
                    .quantity(10)
                    .title("123")
                    .build();

            this.item3 = ItemFactoryForTests.newItemDomain()
                    .toBuilder()
                    .quantity(15)
                    .title("658")
                    .build();

            Flux.just(item1, item2, item3)
                    .flatMap(repository::save)
                    .blockLast();
        }

        @Test
        @DisplayName("when query is empty, then it should return all persisted items sorted by title")
        void whenQueryIsEmpty_shouldReturnAllPersistedItemsSortedByTitle() {
            final ItemQuery itemQuery = ItemQuery.builder().build();
            final Flux<Item> items = repository.findByQuery(itemQuery);

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item2, item1, item3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains 'title', then it should return all items matching it")
        void whenQueryContainsTitle_shouldReturnAllItemsMatchingIt() {
            final ItemQuery query = ItemQuery.builder().title(item1.getTitle()).build();
            final Flux<Item> items = repository.findByQuery(query);

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains 'minQuantity', then it should return all the items which have a quantity greater than or equals to it")
        void whenQueryContainsMinQuantity_shouldReturnAllTheItemsWhichHaveAPriceGreaterThanOrEqualsToIt() {
            final ItemQuery query = ItemQuery.builder().minQuantity(10).build();
            final Flux<Item> items = repository.findByQuery(query);

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item2)
                    .expectNext(item3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when query contains multiple elements, then it should return only the item matching it")
        void whenQueryContainsMultipleElements_shouldReturnOnlyTheItemMatchingIt() {
            final ItemQuery query = ItemQuery.builder()
                    .minQuantity(10)
                    .title(item1.getTitle())
                    .minQuantity(item1.getQuantity())
                    .build();
            final Flux<Item> items = repository.findByQuery(query);

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Item item = ItemFactoryForTests.newItemDomain();

        @BeforeEach
        void setUp() {
            repository.save(item).block();
        }

        @Test
        @DisplayName("when called with existing id, then it should return the matching item wrapped into a Mono")
        void whenCalledWithExistingId_shouldReturnTheMatchingItemWrappedIntoAMono() {
            final Mono<Item> foundItem = repository.findById(item.getId());

            StepVerifier.create(foundItem)
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with unknown id, then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            final Mono<Item> foundItem = repository.findById(UUID.randomUUID().toString());

            StepVerifier.create(foundItem)
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("class: save(Item)")
    class SaveMethod {

        @Test
        @DisplayName("when called and the item was not previously saved, then it should persist the item")
        void whenCalledAndTheItemsWasNotPreviouslySaved_shouldPersistTheItem() {
            final Item item = ItemFactoryForTests.newItemDomain();

            final Mono<Item> mono = repository.save(item)
                    .then(repository.findById(item.getId()));

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(item)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called and the item was previously saved, then it should update the item")
        void whenTheFieldIdMatchesAnExistingItem_shouldThrowAnException() {
            final Item persistedTime = ItemFactoryForTests.newItemDomain();
            final Item newItem = persistedTime.toBuilder().title("newTitle").build();

            final Mono<Item> mono = repository.save(persistedTime)
                    .then(repository.save(newItem))
                    .then(repository.findById(persistedTime.getId()));

            StepVerifier.create(mono)
                    .expectSubscription()
                    .expectNext(newItem)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(ItemDocument.class).block();
    }
}