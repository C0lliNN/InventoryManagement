package com.raphaelcollin.inventorymanagement.infrastructure.clients;

import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.storage.Image;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class AmazonS3ImageStorageClient implements ImageStorageClient {
    private final IdGenerator idGenerator;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public Mono<Image> generatePreSignedUrlForUpload() {
        return Mono.fromCallable(() -> {
            final String identifier = idGenerator.newId();

            final var putRequest = PutObjectRequest
                    .builder()
                    .key(identifier)
                    .bucket(bucket)
                    .contentType(MediaType.IMAGE_JPEG_VALUE)
                    .build();

            final var putPreSignedRequest = PutObjectPresignRequest
                    .builder()
                    .putObjectRequest(putRequest)
                    .signatureDuration(Duration.ofHours(1))
                    .build();

            final String preSignedUrl = s3Presigner
                    .presignPutObject(putPreSignedRequest)
                    .url()
                    .toString();

            return Image
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .build();
        });
    }

    @Override
    public Mono<Image> generatePreSignedUrlForVisualization(final String identifier) {
        return Mono.fromCallable(() -> {
            final var getRequest = GetObjectRequest
                    .builder()
                    .key(identifier)
                    .bucket(bucket)
                    .build();

            final var getPreSignedRequest = GetObjectPresignRequest
                    .builder()
                    .getObjectRequest(getRequest)
                    .signatureDuration(Duration.ofHours(1))
                    .build();

            final String preSignedUrl = s3Presigner
                    .presignGetObject(getPreSignedRequest)
                    .url()
                    .toString();

            return Image
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .build();
        });
    }
}
