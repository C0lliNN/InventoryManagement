package com.raphaelcollin.inventorymanagement.domain;

import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Flux<Product> findByQuery(final ProductQuery query) {
        return productRepository.findByQuery(query);
    }

    public Mono<Product> findById(final String productId) {
        return productRepository
                .findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Product with ID %s was not found", productId))));
    }

    public Mono<Product> save(final Product product) {
        return productRepository.save(product).then(Mono.just(product));
    }

    public Mono<Void> deleteById(final String productId) {
        return productRepository
                .deleteById(productId)
                .filter(result -> result)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Product with ID %s was not found", productId))))
                .then();
    }
}
