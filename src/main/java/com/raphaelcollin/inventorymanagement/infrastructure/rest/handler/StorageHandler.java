package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.raphaelcollin.inventorymanagement.api.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@AllArgsConstructor
@Component
public class StorageHandler {
    private final StorageService storageService;

    public @NonNull Mono<ServerResponse> generatePreSignedUrlForUpload(ServerRequest request) {
        return storageService
                .generatePreSignedUrlForUpload()
                .flatMap(image -> ServerResponse
                        .created(URI.create(image.getPreSignedUrl()))
                        .bodyValue(image)
                );
    }
}
