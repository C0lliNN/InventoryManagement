package com.raphaelcollin.iteminventory.infrastructure.mongodb.serializer;

public interface DocumentSerializer<T, D> {

    T fromDocument(D document);
    D toDocument(T domain);
}
