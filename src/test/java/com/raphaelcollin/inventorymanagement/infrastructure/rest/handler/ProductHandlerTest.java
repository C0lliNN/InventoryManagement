package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.github.javafaker.Faker;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductFactoryForTests;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import com.raphaelcollin.inventorymanagement.domain.storage.Image;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageStorageClient;
import com.raphaelcollin.inventorymanagement.infrastructure.DatabaseTestAutoConfiguration;
import com.raphaelcollin.inventorymanagement.infrastructure.clients.AmazonS3ImageStorageClient;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository.ReactiveMongoProductRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.ProductSerializer;
import com.raphaelcollin.inventorymanagement.utils.extensions.DatabaseRollbackExtension;
import com.raphaelcollin.inventorymanagement.utils.initializers.DatabaseContainerInitializer;
import com.raphaelcollin.inventorymanagement.utils.initializers.LocalstackContainerInitializer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
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

@ContextConfiguration(initializers = {
        DatabaseContainerInitializer.class,
        LocalstackContainerInitializer.class
})
@SpringBootTest(classes = {
        ReactiveMongoProductRepository.class,
        DatabaseTestAutoConfiguration.class,
        AmazonS3ImageStorageClient.class,
        ProductSerializer.class
})
@AutoConfigureWebTestClient
@ComponentScan("com.raphaelcollin.inventorymanagement")
@ExtendWith(DatabaseRollbackExtension.class)
class ProductHandlerTest {
    @Autowired
    private WebTestClient client;

    @SpyBean
    private ReactiveMongoProductRepository productRepository;

    @Autowired
    private ReactiveMongoTemplate template;

    @Autowired
    private ImageStorageClient imageStorageClient;

    private final Product product1 = ProductFactoryForTests.newProductDomain()
            .toBuilder()
            .name("name1")
            .quantity(5)
            .build();
    private final Product product2 = ProductFactoryForTests.newProductDomain()
            .toBuilder()
            .name("name2")
            .quantity(10)
            .build();
    private final Product product3 = ProductFactoryForTests.newProductDomain()
            .toBuilder()
            .name("name3")
            .quantity(15)
            .build();

    private Image image1;
    private Image image2;
    private Image image3;

    private static final String ROOT_URI = "/api/v1/products";

    @BeforeEach
    void setUp() {
        productRepository.save(product1).block();
        productRepository.save(product2).block();
        productRepository.save(product3).block();

        image1 = imageStorageClient.generatePreSignedUrlForVisualization(product1.getImageIdentifier())
                .blockOptional()
                .orElseThrow();

        image2 = imageStorageClient.generatePreSignedUrlForVisualization(product2.getImageIdentifier())
                .blockOptional()
                .orElseThrow();

        image3 = imageStorageClient.generatePreSignedUrlForVisualization(product3.getImageIdentifier())
                .blockOptional()
                .orElseThrow();
    }

    @Nested
    @DisplayName("method: findAllProducts(SearchProducts)")
    class FindAllProductsMethod {

        @Test
        @DisplayName("when called without query params, then it should return all the persisted products")
        void whenCalledWithoutQueryParams_shouldReturnAllThePersistedProducts() {
            client.get()
                    .uri(ROOT_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(product1.getId()))
                    .jsonPath("$[0].sku").value(is(product1.getSku()))
                    .jsonPath("$[0].name").value(is(product1.getName()))
                    .jsonPath("$[0].description").value(is(product1.getDescription()))
                    .jsonPath("$[0].price").value(is(product1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(product1.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(image1.getPreSignedUrl()))
                    .jsonPath("$[1].id").value(is(product2.getId()))
                    .jsonPath("$[1].sku").value(is(product2.getSku()))
                    .jsonPath("$[1].name").value(is(product2.getName()))
                    .jsonPath("$[1].description").value(is(product2.getDescription()))
                    .jsonPath("$[1].price").value(is(product2.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(product2.getQuantity()))
                    .jsonPath("$[1].imageUrl").value(is(image2.getPreSignedUrl()))
                    .jsonPath("$[2].id").value(is(product3.getId()))
                    .jsonPath("$[2].sku").value(is(product3.getSku()))
                    .jsonPath("$[2].name").value(is(product3.getName()))
                    .jsonPath("$[2].description").value(is(product3.getDescription()))
                    .jsonPath("$[2].price").value(is(product3.getPrice().intValue()))
                    .jsonPath("$[2].quantity").value(is(product3.getQuantity()))
                    .jsonPath("$[2].imageUrl").value(is(image3.getPreSignedUrl()));
        }

        @Test
        @DisplayName("when called with name query param, then it should return only products matching it")
        void whenCalledWithNameQueryParam_shouldReturnOnlyProductsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("name", product1.getName())
                    .toUriString();

            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(product1.getId()))
                    .jsonPath("$[0].sku").value(is(product1.getSku()))
                    .jsonPath("$[0].name").value(is(product1.getName()))
                    .jsonPath("$[0].description").value(is(product1.getDescription()))
                    .jsonPath("$[0].price").value(is(product1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(product1.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(image1.getPreSignedUrl()));
        }

        @Test
        @DisplayName("when called with minQuantity query param, then it should return only products matching it")
        void whenCalledWithMinQuantityQueryParam_shouldReturnOnlyProductsMatchingIt() {
            final String uri = UriComponentsBuilder.fromUriString(ROOT_URI)
                    .queryParam("minQuantity", product2.getQuantity())
                    .toUriString();

            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(product2.getId()))
                    .jsonPath("$[0].sku").value(is(product2.getSku()))
                    .jsonPath("$[0].name").value(is(product2.getName()))
                    .jsonPath("$[0].description").value(is(product2.getDescription()))
                    .jsonPath("$[0].price").value(is(product2.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(product2.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(image2.getPreSignedUrl()))
                    .jsonPath("$[1].id").value(is(product3.getId()))
                    .jsonPath("$[1].name").value(is(product3.getName()))
                    .jsonPath("$[1].description").value(is(product3.getDescription()))
                    .jsonPath("$[1].price").value(is(product3.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(product3.getQuantity()))
                    .jsonPath("$[1].imageUrl").value(is(image3.getPreSignedUrl()));
        }
    }

    @Nested
    @DisplayName("method: findProductById(String)")
    class FindProductByIdMethod {

        @Test
        @DisplayName("when called with existing id, then it should return the matching product")
        void whenCalledWithExistingId_shouldReturnTheMatchingProduct() {
            client.get()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").value(is(product1.getId()))
                    .jsonPath("$.sku").value(is(product1.getSku()))
                    .jsonPath("$.name").value(is(product1.getName()))
                    .jsonPath("$.description").value(is(product1.getDescription()))
                    .jsonPath("$.price").value(is(product1.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(product1.getQuantity()))
                    .jsonPath("$.imageUrl").value(is(image1.getPreSignedUrl()));
        }

        @Test
        @DisplayName("when called with unknown id, then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            client.get()
                    .uri(ROOT_URI + "/productId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is("Product with ID productId was not found"))
                    .jsonPath("$.details").isEmpty();
        }
    }

    @Nested
    @DisplayName("method: saveProduct(CreateProduct)")
    class SaveProductMethod {
        private final Faker faker = Faker.instance();

        @Test
        @DisplayName("when called without name, then it should return 400 error")
        void whenCalledWithoutName_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    null,
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid name, then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.lorem().fixedString(155),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called without sku, then it should return 400 error")
        void whenCalledWithoutSku_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    null,
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid sku, then it should return 400 error")
        void whenCalledWithInvalidSku_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    faker.lorem().fixedString(21),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 20 characters"));
        }

        @Test
        @DisplayName("when called without description, then it should return 400 error")
        void whenCalledWithoutDescription_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    null,
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
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
            final CreateProduct createProduct = new CreateProduct(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().fixedString(1005),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
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
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    null,
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
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
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(price),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
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
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    null,
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
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
            final CreateProduct createProduct = new CreateProduct(
                    faker.commerce().productName(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    -1,
                    faker.internet().uuid()
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called without imageIdentifier, then it should return 400 error")
        void whenCalledWithoutImageIdentifier_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    null
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("imageIdentifier"))
                    .jsonPath("$.details[0].message").value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid imageIdentifier, then it should return 400 error")
        void whenCalledWithInvalidImageIdentifier_shouldReturn400Error() {
            final CreateProduct createProduct = new CreateProduct(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid().repeat(2)
            );

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("imageIdentifier"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName("when called with valid payload, then it should return 201 and persist the product")
        void whenCalledWithValidPayload_shouldReturn201AndPersistTheProduct() {
            final CreateProduct createProduct = ProductFactoryForTests.newCreateProductDto();
            final Image image = imageStorageClient.generatePreSignedUrlForVisualization(createProduct.getImageIdentifier())
                    .blockOptional()
                    .orElseThrow();

            client.post()
                    .uri(ROOT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createProduct)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").value(is(createProduct.getName()))
                    .jsonPath("$.sku").value(is(createProduct.getSku()))
                    .jsonPath("$.description").value(is(createProduct.getDescription()))
                    .jsonPath("$.price").value(is(createProduct.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(createProduct.getQuantity()))
                    .jsonPath("$.imageUrl").value(is(image.getPreSignedUrl()));

            StepVerifier.create(productRepository.findByQuery(ProductQuery.builder().build()))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();

        }
    }

    @Nested
    @DisplayName("method: updateProductById(String, UpdateProduct)")
    class UpdateProductByIdMethod {
        final Faker faker = Faker.instance();

        @Test
        @DisplayName("when called with invalid name, then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    faker.lorem().fixedString(151),
                    null,
                    null,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with valid name, then it should return 204 and update it")
        void whenCalledWithValidName_shouldReturn204AndUpdateIt() {
            final String newName = faker.lorem().sentence();

            final UpdateProduct updateProduct = new UpdateProduct(
                    newName,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().name(newName).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid sku, then it should return 400 error")
        void whenCalledWithInvalidSku_shouldReturn400Error() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    faker.lorem().fixedString(21),
                    null,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 20 characters"));
        }

        @Test
        @DisplayName("when called with valid sku, then it should return 204 and update it")
        void whenCalledWithValidSku_shouldReturn204AndUpdateIt() {
            final String newSku = faker.lorem().fixedString(8);

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    newSku,
                    null,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().sku(newSku).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid description, then it should return 400 error")
        void whenCalledWithInvalidDescription_shouldReturn400Error() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    faker.lorem().fixedString(1001),
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
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

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    newDescription,
                    null,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().description(newDescription).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @ParameterizedTest
        @ValueSource(doubles = {-23.8, -1.0, 0.0})
        @DisplayName("when called with invalid price, then it should return 400 error")
        void whenCalledWithInvalidPrice_shouldReturn400Error(double price) {
            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(price),
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
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

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    newPrice,
                    null,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().price(newPrice).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid quantity, then it should return 400 error")
        void whenCalledWithInvalidQuantity_shouldReturn400Error() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    -1,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
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

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    newQuantity,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().quantity(newQuantity).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with invalid imageIdentifier, then it should return 400 error")
        void whenCalledWithInvalidImageIdentifier_shouldReturn400Error() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    null,
                    faker.internet().uuid().repeat(2)
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("imageIdentifier"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName("when called with valid imageIdentifier, then it should return 204 and update it")
        void whenCalledWithValidImageIdentifier_shouldReturn204AndUpdateIt() {
            final String newImageIdentifier = faker.internet().uuid();

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    null,
                    newImageIdentifier
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().imageIdentifier(newImageIdentifier).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when called with multiple invalid fields, then it should return 400")
        void whenCalledWithMultipleInvalidFields_shouldReturn400() {
            final UpdateProduct updateProduct = new UpdateProduct(
                    faker.lorem().fixedString(151),
                    null,
                    faker.lorem().fixedString(1051),
                    null,
                    -1,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message").value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[1].field").value(is("name"))
                    .jsonPath("$.details[1].message").value(is("the field must not exceed 150 characters"))
                    .jsonPath("$.details[2].field").value(is("quantity"))
                    .jsonPath("$.details[2].message").value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with multiple valid fields, then it should return 204 and update all of them")
        void whenCalledWithMultipleValidFields_shouldReturn204AndUpdateAllOfThem() {
            final String newName = faker.lorem().sentence();
            final int newQuantity = faker.random().nextInt(1, 100);

            final UpdateProduct updateProduct = new UpdateProduct(
                    newName,
                    null,
                    null,
                    null,
                    newQuantity,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            final Product expectedProduct = product1.toBuilder().name(newName).quantity(newQuantity).build();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .expectNext(expectedProduct)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when some unexpected error is throw, then it should return 500")
        void whenSomeUnexpectedErrorIsThrown_shouldReturn500() {
            doThrow(RuntimeException.class)
                    .when(productRepository)
                    .findById(product1.getId());

            final UpdateProduct updateProduct = new UpdateProduct(
                    null,
                    null,
                    null,
                    null,
                    90,
                    null
            );

            client.patch()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateProduct)
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody()
                    .jsonPath("$.message").value(is("There's been an unexpected error. Please, contact support."))
                    .jsonPath("$.details").isEmpty();
        }
    }

    @Nested
    @DisplayName("method: deleteProductById(String)")
    class DeleteProductByIdMethod {

        @Test
        @DisplayName("when called with unknown id, then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            final String unknownId = UUID.randomUUID().toString();

            client.delete()
                    .uri(ROOT_URI + "/" + unknownId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").value(is(format("Product with ID %s was not found", unknownId)))
                    .jsonPath("$.details").isEmpty();
        }

        @Test
        @DisplayName("when called with existing id, then it should return 204 and delete the product in the db")
        void whenCalledWithExistingId_shouldReturn204AndDeleteTheProductInTheDB() {
            client.delete()
                    .uri(ROOT_URI + "/" + product1.getId())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            StepVerifier.create(productRepository.findById(product1.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }
    }
}