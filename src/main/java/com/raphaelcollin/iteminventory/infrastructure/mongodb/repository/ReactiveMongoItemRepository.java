package com.raphaelcollin.iteminventory.infrastructure.mongodb.repository;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemQuery;
import com.raphaelcollin.iteminventory.domain.ItemRepository;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer.DocumentSerializer;
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
public class ReactiveMongoItemRepository implements ItemRepository {
    private final ReactiveMongoTemplate mongoTemplate;
    private final DocumentSerializer<Item, ItemDocument> serializer;

    @Override
    public Flux<Item> findByQuery(final ItemQuery itemQuery) {
        final Query mongoQuery = new Query();

        itemQuery.getTitle()
                .ifPresent(title -> mongoQuery.addCriteria(new Criteria("title").is(title)));

        itemQuery.getMinQuantity()
                .ifPresent(quantity -> mongoQuery.addCriteria(new Criteria("quantity").gte(quantity)));

        return mongoTemplate
                .query(ItemDocument.class)
                .matching(mongoQuery)
                .all()
                .sort(Comparator.comparing(ItemDocument::getTitle))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Item> findById(final String itemId) {
        return mongoTemplate
                .findById(itemId, ItemDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final Item item) {
        return mongoTemplate
                .save(serializer.toDocument(item))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String itemId) {
        final Query query = new Query(new Criteria("id").is(itemId));

        return mongoTemplate
                .remove(query, ItemDocument.class)
                .map(deleteResult -> deleteResult.getDeletedCount() > 0);

    }
}
