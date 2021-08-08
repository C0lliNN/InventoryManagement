package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.out.Image;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageStorageClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class StorageService {
    private final ImageStorageClient imageStorageClient;

    public Mono<Image> generatePreSignedUrlForUpload() {
        return imageStorageClient
                .generatePreSignedUrlForUpload()
                .map(Image::fromDomain);
    }
}
