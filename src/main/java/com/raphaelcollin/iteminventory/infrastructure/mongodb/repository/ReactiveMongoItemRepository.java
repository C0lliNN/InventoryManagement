package com.raphaelcollin.iteminventory.infrastructure.mongodb.repository;

import com.raphaelcollin.iteminventory.domain.Item;
import com.raphaelcollin.iteminventory.domain.ItemRepository;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.document.ItemDocument;
import com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer.DocumentSerializer;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class ReactiveMongoItemRepository implements ItemRepository {
    private final ReactiveMongoTemplate mongoTemplate;
    private final DocumentSerializer<Item, ItemDocument> serializer;

    @Override
    public Flux<Item> findAllItems() {
        return mongoTemplate
                .findAll(ItemDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Item> findItemById(final String itemId) {
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
}
