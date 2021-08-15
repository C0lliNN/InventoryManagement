package com.raphaelcollin.inventorymanagement.utils.extensions;

import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseRollbackExtension implements BeforeAllCallback, AfterEachCallback {

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        cleanTables(SpringExtension.getApplicationContext(extensionContext));
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) {
        cleanTables(SpringExtension.getApplicationContext(extensionContext));
    }

    private void cleanTables(ApplicationContext applicationContext) {
        final var mongoTemplate = applicationContext.getBean(ReactiveMongoTemplate.class);

        mongoTemplate.dropCollection(CategoryDocument.class).block();
        mongoTemplate.dropCollection(ProductDocument.class).block();
    }
}