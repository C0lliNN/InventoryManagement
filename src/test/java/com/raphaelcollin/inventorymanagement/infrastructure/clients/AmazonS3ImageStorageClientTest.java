package com.raphaelcollin.inventorymanagement.infrastructure.clients;

import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.infrastructure.config.AmazonS3Config;
import com.raphaelcollin.inventorymanagement.utils.initializers.LocalstackContainerInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                AmazonS3Config.class,
                PropertyPlaceholderAutoConfiguration.class,
                IdGenerator.class,
                AmazonS3ImageStorageClient.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("dev")
@ContextConfiguration(initializers = LocalstackContainerInitializer.class)
class AmazonS3ImageStorageClientTest {

    @Autowired
    private AmazonS3ImageStorageClient imageStorageClient;

    @SpyBean
    private IdGenerator idGenerator;

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload()")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName("when called, then it should generate identifier and url")
        void whenCalled_shouldGenerateIdentifierAndUrl() {
            final String identifier = UUID.randomUUID().toString();

            when(idGenerator.newId()).thenReturn(identifier);

            StepVerifier.create(imageStorageClient.generatePreSignedUrlForUpload())
                    .expectSubscription()
                    .assertNext(image -> {
                        assertThat(image.getIdentifier()).isEqualTo(identifier);
                        assertThat(image.getPreSignedUrl()).contains(identifier);
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: generatePreSignedUrlForVisualization(String)")
    class GeneratePreSignedUrlForVisualizationMethod {

        @Test
        @DisplayName("when called, then it should generate a url for the given identifier")
        void whenCalled_shouldGenerateAUrlForTheGivenIdentifier() {
            final String identifier = UUID.randomUUID().toString();

            StepVerifier.create(imageStorageClient.generatePreSignedUrlForVisualization(identifier))
                    .expectSubscription()
                    .assertNext(image -> {
                        assertThat(image.getIdentifier()).isEqualTo(identifier);
                        assertThat(image.getPreSignedUrl()).contains(identifier);
                    })
                    .verifyComplete();

            verifyNoInteractions(idGenerator);
        }
    }
}