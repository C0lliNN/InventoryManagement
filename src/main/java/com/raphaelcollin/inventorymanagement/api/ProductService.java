package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.out.Product;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;
    private final RequestValidator validator;

    public Flux<Product> findProducts(final SearchProducts searchProducts) {
        return productRepository
                .findByQuery(searchProducts.toDomain())
                .map(Product::fromDomain);
    }

    public Mono<Product> findById(final String productId) {
        return productRepository
                .findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Product with ID %s was not found", productId))))
                .map(Product::fromDomain);
    }

    public Mono<Product> save(final CreateProduct createProduct) {
        return validator
                .validate(createProduct)
                .map(create -> create.toDomain(idGenerator.newId()))
                .flatMap(product -> productRepository.save(product).thenReturn(product))
                .map(Product::fromDomain);
    }

    public Mono<Void> updateById(final String productId, final UpdateProduct updateProduct) {
        return validator
                .validate(updateProduct)
                .flatMap(update -> productRepository.findById(productId))
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Product with ID %s was not found", productId))))
                .map(updateProduct::toDomain)
                .flatMap(productRepository::save)
                .then();
    }

    public Mono<Void> deleteById(final String productId) {
        return productRepository
                .deleteById(productId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(format("Product with ID %s was not found", productId)))).then();
    }
}
