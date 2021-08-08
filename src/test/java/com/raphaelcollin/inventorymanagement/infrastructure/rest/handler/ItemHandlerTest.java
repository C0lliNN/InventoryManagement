package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateItem;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateItem;
import com.raphaelcollin.inventorymanagement.domain.Item;
import com.raphaelcollin.inventorymanagement.domain.ItemFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ItemQuery;
import com.raphaelcollin.inventorymanagement.infrastructure.DatabaseTestAutoConfiguration;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository.ReactiveMongoItemRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.ItemSerializer;
import com.raphaelcollin.inventorymanagement.utils.initializers.DatabaseContainerInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;

@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {
        ReactiveMongoItemRepository.class,
        DatabaseTestAutoConfiguration.class,
        ItemSerializer.class
})
@AutoConfigureWebTestClient
@ComponentScan("com.raphaelcollin.inventorymanagement")
class ItemHandlerTest {
    @Autowired
    private WebTestClient client;

    @SpyBean
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
            client.get()
                    .uri(ROOT_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(item1.getId()))
                    .jsonPath("$[0].title").value(is(item1.getTitle()))
                    .jsonPath("$[0].description").value(is(item1.getDescription()))
                    .jsonPath("$[0].price").value(is(item1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(item1.getQuantity()))
                    .jsonPath("$[1].id").value(is(item2.getId()))
                    .jsonPath("$[1].title").value(is(item2.getTitle()))
                    .jsonPath("$[1].description").value(is(item2.getDescription()))
                    .jsonPath("$[1].price").value(is(item2.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(item2.getQuantity()))
                    .jsonPath("$[2].id").value(is(item3.getId()))
                    .jsonPath("$[2].title").value(is(item3.getTitle()))
                    .jsonPath("$[2].description").value(is(item3.getDescription()))
                    .jsonPath("$[2].price").value(is(item3.getPrice().intValue()))
                    .jsonPath("$[2].quantity").value(is(item3.getQuantity()));
        }

        @Test
        @DisplayName("when called with title query param, then it should return only items matching it")
        void whenCalledWithTitleQueryParam_shouldReturnOnlyItemsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("title", item1.getTitle())
                    .toUriString();

            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(item1.getId()))
                    .jsonPath("$[0].title").value(is(item1.getTitle()))
                    .jsonPath("$[0].description").value(is(item1.getDescription()))
                    .jsonPath("$[0].price").value(is(item1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(item1.getQuantity()));
        }

        @Test
        @DisplayName("when called with minQuantity query param, then it should return only items matching it")
        void whenCalledWithMinQuantityQueryParam_shouldReturnOnlyItemsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("minQuantity", item2.getQuantity())
                    .toUriString();

            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(item2.getId()))
                    .jsonPath("$[0].title").value(is(item2.getTitle()))
                    .jsonPath("$[0].description").value(is(item2.getDescription()))
                    .jsonPath("$[0].price").value(is(item2.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(item2.getQuantity()))
                    .jsonPath("$[1].id").value(is(item3.getId()))
                    .jsonPath("$[1].title").value(is(item3.getTitle()))
                    .jsonPath("$[1].description").value(is(item3.getDescription()))
                    .jsonPath("$[1].price").value(is(item3.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(item3.getQuantity()));
        }
    }

    @Nested
    @DisplayName("method: findItemById(String)")
    class FindItemByIdMethod {

        @Test
        @DisplayName("when called with existing id, then it should return the matching item")
        void whenCalledWithExistingId_shouldReturnTheMatchingItem() {
            client.get()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").value(is(item1.getId()))
                    .jsonPath("$.title").value(is(item1.getTitle()))
                    .jsonPath("$.description").value(is(item1.getDescription()))
                    .jsonPath("$.price").value(is(item1.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(item1.getQuantity()));
        }

        @Test
        @DisplayName("when called with unknown id, then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            client.get()
                    .uri(ROOT_URI + "/itemId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is("Item with ID itemId was not found"))
                    .jsonPath("$.details").isEmpty();
        }
    }

    @Nested
    @DisplayName("method: saveItem(CreateItem)")
    class SaveItemMethod {
        private final Faker faker = Faker.instance();

        @Test
        @DisplayName("when called without title, then it should return 400 error")
        void whenCalledWithoutTitle_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    null,
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("title"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid title, then it should return 400 error")
        void whenCalledWithInvalidTitle_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().fixedString(155),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("title"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called without description, then it should return 400 error")
        void whenCalledWithoutDescription_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    null,
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid description, then it should return 400 error")
        void whenCalledWithInvalidDescription_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(1005),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 1000 characters"));
        }

        @Test
        @DisplayName("when called without price, then it should return 400 error")
        void whenCalledWithoutPrice_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    faker.lorem().sentence(),
                    null,
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("price"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {-29, -1, 0.0})
        @DisplayName("when called with invalid price, then it should return 400 error")
        void whenCalledWithInvalidPrice_shouldReturn400Error(double price) {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(price),
                    faker.random().nextInt(4, 20)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("price"))
                    .jsonPath("$.details[0].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called without quantity, then it should return 400 error")
        void whenCalledWithoutQuantity_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    null
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid quantity, then it should return 400 error")
        void whenCalledWithInvalidQuantity_shouldReturn400Error() {
            final CreateItem createItem = new CreateItem(
                    faker.lorem().characters(),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    -1
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with valid payload, then it should return 201 and persist the item")
        void whenCalledWithValidPayload_shouldReturn201AndPersistTheItem() {
            final CreateItem createItem = ItemFactoryForTests.newCreateItemDto();

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createItem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.title").value(is(createItem.getTitle()))
                    .jsonPath("$.description").value(is(createItem.getDescription()))
                    .jsonPath("$.price").value(is(createItem.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(createItem.getQuantity()));

            StepVerifier.create(itemRepository.findByQuery(ItemQuery.builder().build()))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();

        }
    }

    @Nested
    @DisplayName("method: updateItemById(String, UpdateItem)")
    class UpdateItemByIdMethod {
        final Faker faker = Faker.instance();

        @Test
        @DisplayName("when called with invalid title, then it should return 400 error")
        void whenCalledWithInvalidTitle_shouldReturn400Error() {
            final UpdateItem updateItem = new UpdateItem(
                    faker.lorem().fixedString(151),
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("title"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with valid title, then it should return 204 and update it")
        void whenCalledWithValidTitle_shouldReturn204AndUpdateIt() {
            final String newTitle = faker.lorem().sentence();

            final UpdateItem updateItem = new UpdateItem(
                    newTitle,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Item expectedItem = item1.toBuilder().title(newTitle).build();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(expectedItem)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid description, then it should return 400 error")
        void whenCalledWithInvalidDescription_shouldReturn400Error() {
            final UpdateItem updateItem = new UpdateItem(
                    null,
                    faker.lorem().fixedString(1001),
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 1000 characters"));
        }

        @Test
        @DisplayName("when called with valid description, then it should return 204 and update it")
        void whenCalledWithValidDescription_shouldReturn204AndUpdateIt() {
            final String newDescription = faker.lorem().sentence();

            final UpdateItem updateItem = new UpdateItem(
                    null,
                    newDescription,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Item expectedItem = item1.toBuilder().description(newDescription).build();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(expectedItem)
                    .verifyComplete();
        }

        @ParameterizedTest
        @ValueSource(doubles = {-23.8, -1.0, 0.0})
        @DisplayName("when called with invalid price, then it should return 400 error")
        void whenCalledWithInvalidPrice_shouldReturn400Error(double price) {
            final UpdateItem updateItem = new UpdateItem(
                    null,
                    null,
                    BigDecimal.valueOf(price),
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("price"))
                    .jsonPath("$.details[0].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with valid price, then it should return 204 and update it")
        void whenCalledWithValidPrice_shouldReturn204AndUpdateIt() {
            final BigDecimal newPrice = BigDecimal.valueOf(faker.random().nextInt(1, 100));

            final UpdateItem updateItem = new UpdateItem(
                    null,
                    null,
                    newPrice,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Item expectedItem = item1.toBuilder().price(newPrice).build();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(expectedItem)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid quantity, then it should return 400 error")
        void whenCalledWithInvalidQuantity_shouldReturn400Error() {
            final UpdateItem updateItem = new UpdateItem(
                    null,
                    null,
                    null,
                    -1
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with valid quantity, then it should return 204 and update it")
        void whenCalledWithValidQuantity_shouldReturn204AndUpdateIt() {
            final int newQuantity = faker.random().nextInt(1, 100);

            final UpdateItem updateItem = new UpdateItem(
                    null,
                    null,
                    null,
                    newQuantity
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Item expectedItem = item1.toBuilder().quantity(newQuantity).build();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(expectedItem)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with multiple invalid fields, then it should return 400")
        void whenCalledWithMultipleInvalidFields_shouldReturn400() {
            final UpdateItem updateItem = new UpdateItem(
                    faker.lorem().fixedString(151),
                    faker.lorem().fixedString(1051),
                    null,
                    -1
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[1].field").value(is("quantity"))
                    .jsonPath("$.details[1].message").value(is("the field must contain a positive value"))
                    .jsonPath("$.details[2].field").value(is("title"))
                    .jsonPath("$.details[2].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with multiple valid fields, then it should return 204 and update all of them")
        void whenCalledWithMultipleValidFields_shouldReturn204AndUpdateAllOfThem() {
            final String newTitle = faker.lorem().sentence();
            final int newQuantity = faker.random().nextInt(1, 100);

            final UpdateItem updateItem = new UpdateItem(
                    newTitle,
                    null,
                    null,
                    newQuantity
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Item expectedItem = item1.toBuilder().title(newTitle).quantity(newQuantity).build();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .expectNext(expectedItem)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when some unexpected error is throw, then it should return 500")
        void when() {
            doThrow(RuntimeException.class)
                    .when(itemRepository)
                    .findById(item1.getId());

            final UpdateItem updateItem = new UpdateItem(
                    null,
                    null,
                    null,
                    90
            );

            client.patch()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateItem)
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody()
                    .jsonPath("$.message").value(is("There's been an unexpected error. Please, contact support."))
                    .jsonPath("$.details").isEmpty();
        }
    }

    @Nested
    @DisplayName("method: deleteItemById(String)")
    class DeleteItemByIdMethod {

        @Test
        @DisplayName("when called with unknown id, then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            final String unknownId = UUID.randomUUID().toString();

            client.delete()
                    .uri(ROOT_URI + "/" + unknownId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is(format("Item with ID %s was not found", unknownId)))
                    .jsonPath("$.details").isEmpty();
        }

        @Test
        @DisplayName("when called with existing id, then it should return 204 and delete the item in the db")
        void whenCalledWithExistingId_shouldReturn204AndDeleteTheItemInTheDB() {
            client.delete()
                    .uri(ROOT_URI + "/" + item1.getId())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            StepVerifier.create(itemRepository.findById(item1.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(ItemDocument.class).block();
    }
}