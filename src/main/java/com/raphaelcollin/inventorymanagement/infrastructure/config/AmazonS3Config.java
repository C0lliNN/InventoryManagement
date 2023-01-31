package com.raphaelcollin.inventorymanagement.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class AmazonS3Config {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.endpoint:#{null}}")
    private String s3Endpoint;

    @Bean
    public S3Presigner s3Presigner() {
        S3Presigner.Builder builder = S3Presigner
                .builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(awsRegion));

        if (s3Endpoint != null) {
            builder = builder.endpointOverride(URI.create(s3Endpoint));
        }

        return builder.build();
    }
}
