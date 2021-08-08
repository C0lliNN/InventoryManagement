package com.raphaelcollin.inventorymanagement.utils.initializers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;

import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@ContextConfiguration
public class LocalstackContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:0.12.11");

    private static final LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(S3)
            .withEnv("DEFAULT_REGION", Region.US_EAST_2.toString())
            .withExposedPorts(4566)
            .withClasspathResourceMapping("localstack/init-scripts/setup_localstack.sh", "/docker-entrypoint-initaws.d/init.sh", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*Initialization has finished\\!\n", 1));

    static {
        LOCALSTACK_CONTAINER.start();

        System.setProperty("aws.accessKeyId", LOCALSTACK_CONTAINER.getAccessKey());
        System.setProperty("aws.secretKey", LOCALSTACK_CONTAINER.getSecretKey());
    }

    @Override
    public void initialize(@NonNull final ConfigurableApplicationContext applicationContext) {
        final PropertySource<Map<String, Object>> properties = new MapPropertySource(
                "AwsLocalStackInitializer",
                Map.of(
                        "aws.region", LOCALSTACK_CONTAINER.getRegion(),
                        "aws.s3.endpoint", LOCALSTACK_CONTAINER.getEndpointOverride(S3).toString()
                )
        );

        applicationContext
                .getEnvironment()
                .getPropertySources()
                .addFirst(properties);
    }
}
