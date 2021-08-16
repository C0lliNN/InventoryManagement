package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.repository;

import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document.ProductDocument;
import com.raphaelcollin.inventorymanagement.infrastructure.mongodb.serializer.DocumentSerializer;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Repository
@AllArgsConstructor
public class ReactiveMongoProductRepository implements ProductRepository {
    private final ReactiveMongoTemplate mongoTemplate;
    private final DocumentSerializer<Product, ProductDocument> serializer;

    @Override
    public Flux<Product> findByQuery(final ProductQuery productQuery) {
        final Query mongoQuery = new Query();

        productQuery.getName()
                .ifPresent(name -> mongoQuery.addCriteria(new Criteria("name").is(name)));

        productQuery.getCategoryId()
                        .ifPresent(categoryId -> mongoQuery.addCriteria(new Criteria("category.id").is(categoryId)));

        productQuery.getMinQuantity()
                .ifPresent(quantity -> mongoQuery.addCriteria(new Criteria("quantity").gte(quantity)));

        return mongoTemplate
                .query(ProductDocument.class)
                .matching(mongoQuery)
                .all()
                .sort(Comparator.comparing(ProductDocument::getName))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Product> findById(final String productId) {
        return mongoTemplate
                .findById(productId, ProductDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final Product product) {
        return mongoTemplate
                .save(serializer.toDocument(product))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String productId) {
        final Query query = new Query(new Criteria("id").is(productId));

        return mongoTemplate
                .remove(query, ProductDocument.class)
                .map(deleteResult -> deleteResult.getDeletedCount() > 0);

    }
}
