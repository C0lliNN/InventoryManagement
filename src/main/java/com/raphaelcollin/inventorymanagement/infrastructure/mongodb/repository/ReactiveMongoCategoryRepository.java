package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.CategoryDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.DocumentSerializer;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@AllArgsConstructor
@Repository
public class ReactiveMongoCategoryRepository implements CategoryRepository {
    private final ReactiveMongoTemplate mongoTemplate;
    private final DocumentSerializer<Category, CategoryDocument> serializer;

    @Override
    public Flux<Category> findAll() {
        return mongoTemplate
                .findAll(CategoryDocument.class)
                .sort(Comparator.comparing(CategoryDocument::getName))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Category> findById(final String categoryId) {
        return mongoTemplate
                .findById(categoryId, CategoryDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final Category category) {
        return mongoTemplate
                .save(serializer.toDocument(category))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String categoryId) {
        final Query query = new Query(new Criteria("id").is(categoryId));

        return mongoTemplate
                .remove(query, CategoryDocument.class)
                .map(deleteResult -> deleteResult.getDeletedCount() > 0);
    }
}
