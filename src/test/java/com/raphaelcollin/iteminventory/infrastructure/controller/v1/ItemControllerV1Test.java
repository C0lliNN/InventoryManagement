package com.raphaelcollin.iteminventory.infrastructure.controller.v1;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemFactoryForTests;
import com.raphaelcollin.iteminventory.infranstructure.config.DatabaseTestAutoConfiguration;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.repository.ReactiveMongoItemRepository;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer.ItemSerializer;
import com.raphaelcollin.iteminventory.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {
        ReactiveMongoItemRepository.class,
        DatabaseTestAutoConfiguration.class,
        ItemSerializer.class
})
@AutoConfigureWebTestClient
@ComponentScan("com.raphaelcollin.iteminventory")
class ItemControllerV1Test {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReactiveMongoItemRepository itemRepository;

    @Autowired
    private ReactiveMongoTemplate template;

    private final Item item1 = ItemFactoryForTests.newItemDomain()
            .toBuilder()
            .title("title1")
            .quantity(5)
            .build();
    private final Item item2 = ItemFactoryForTests.newItemDomain()
            .toBuilder()
            .title("title2")
            .quantity(10)
            .build();
    private final Item item3 = ItemFactoryForTests.newItemDomain()
            .toBuilder()
            .title("title3")
            .quantity(15)
            .build();

    private static final String ROOT_URI = "/api/v1/items";

    @BeforeEach
    void setUp() {
        itemRepository.save(item1).block();
        itemRepository.save(item2).block();
        itemRepository.save(item3).block();
    }

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findAllItems(SearchItems)")
    class FindAllItemsMethod {

        @Test
        @DisplayName("when called without query params, then it should return all the persisted items")
        void whenCalledWithoutQueryParams_shouldReturnAllThePersistedItems() {
            final Flux<Item> items = client.get()
                    .uri(ROOT_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(Item.class)
                    .getResponseBody();

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item1, item2, item3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with title query param, then it should return only items matching it")
        void whenCalledWithTitleQueryParam_shouldReturnOnlyItemsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("title", item1.getTitle())
                    .toUriString();

            final Flux<Item> items = client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(Item.class)
                    .getResponseBody();

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with minQuantity query param, then it should return only items matching it")
        void whenCalledWithMinQuantityQueryParam_shouldReturnOnlyItemsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("minQuantity", item2.getQuantity())
                    .toUriString();

            final Flux<Item> items = client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(Item.class)
                    .getResponseBody();

            StepVerifier.create(items)
                    .expectSubscription()
                    .expectNext(item2, item3)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(ItemDocument.class).block();
    }
}