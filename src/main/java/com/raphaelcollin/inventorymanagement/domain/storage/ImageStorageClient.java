package com.raphaelcollin.inventorymanagement.domain.storage;

import reactor.core.publisher.Mono;

public interface ImageStorageClient {
    Mono<Image> generatePreSignedUrlForUpload();
    Mono<Image> generatePreSignedUrlForVisualization(String identifier);
}
