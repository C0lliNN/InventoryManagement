package com.raphaelcollin.iteminventory.infrastructure.rest.handler;

import com.raphaelcollin.iteminventory.utils.initializers.LocalstackContainerInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.hasSize;

@ContextConfiguration(initializers = LocalstackContainerInitializer.class)
@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureWebTestClient
class StorageHandlerTest {

    @Autowired
    private WebTestClient client;

    private static final String ROOT_ENDPOINT = "/api/v1/storage";

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload(ServerRequest)")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName("when called, then it should generate a preSignedUrl and return 201")
        void whenCalled_shouldGenerateAPreSignedUrlAndReturn201() {
            client.post()
                    .uri(ROOT_ENDPOINT)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue("")
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.identifier", hasSize(36)).hasJsonPath()
                    .jsonPath("$.preSignedUrl").isNotEmpty();

        }
    }
}