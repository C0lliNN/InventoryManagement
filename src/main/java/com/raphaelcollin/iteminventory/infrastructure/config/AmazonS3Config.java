package com.raphaelcollin.iteminventory.infrastructure.config;

import org.apache.commons.lang3.StringUtils;
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

    @Value("${aws.s3.endpoint:}")
    private String s3Endpoint;

    @Bean
    public S3Presigner s3Presigner() {
        final var s3PreSignerBuilder = S3Presigner
                .builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(awsRegion));

        if (StringUtils.isNotBlank(s3Endpoint)) {
            s3PreSignerBuilder.endpointOverride(URI.create(s3Endpoint));
        }

        return s3PreSignerBuilder.build();
    }
}
