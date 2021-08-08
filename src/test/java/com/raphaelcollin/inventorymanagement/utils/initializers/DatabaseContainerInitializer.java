package com.raphaelcollin.inventorymanagement.utils.initializers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

@ContextConfiguration
public class DatabaseContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String MONGO_DB_IMAGE = "mongo";

    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(MONGO_DB_IMAGE);

    static {
        MONGO_DB_CONTAINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final PropertySource<Map<String, Object>> properties = new MapPropertySource(
                "DatabaseContainerInitializer",
                Map.of(
                        "spring.data.mongodb.uri", MONGO_DB_CONTAINER.getReplicaSetUrl("test")
                )
        );

        applicationContext
                .getEnvironment()
                .getPropertySources()
                .addFirst(properties);
    }
}

