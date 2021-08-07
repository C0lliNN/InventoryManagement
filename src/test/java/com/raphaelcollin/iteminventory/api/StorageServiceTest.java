package com.raphaelcollin.iteminventory.api;

import com.raphaelcollin.iteminventory.domain.storage.Image;
import com.raphaelcollin.iteminventory.domain.storage.ImageFactoryForTests;
import com.raphaelcollin.iteminventory.domain.storage.ImageStorageClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    @InjectMocks
    private StorageService storageService;

    @Mock
    private ImageStorageClient imageStorageClient;

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload()")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName("when called, then it should forward the call to the underlying client")
        void whenCalled_shouldForwardTheCallToTheUnderlyingClient() {
            final Image image = ImageFactoryForTests.newImageDomain();

            when(imageStorageClient.generatePreSignedUrlForUpload()).thenReturn(Mono.just(image));

            StepVerifier.create(storageService.generatePreSignedUrlForUpload())
                    .expectSubscription()
                    .expectNext(com.raphaelcollin.iteminventory.api.dto.out.Image.fromDomain(image))
                    .verifyComplete();

            verify(imageStorageClient).generatePreSignedUrlForUpload();
            verifyNoMoreInteractions(imageStorageClient);
        }
    }
}