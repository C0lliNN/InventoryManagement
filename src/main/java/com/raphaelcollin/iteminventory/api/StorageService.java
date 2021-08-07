package com.raphaelcollin.iteminventory.api;

import com.raphaelcollin.iteminventory.api.dto.out.Image;
import com.raphaelcollin.iteminventory.domain.storage.ImageStorageClient;
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
