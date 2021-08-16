package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.out.Product;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import com.raphaelcollin.inventorymanagement.domain.storage.ImageStorageClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageClient imageStorageClient;
    private final IdGenerator idGenerator;
    private final RequestValidator validator;

    public Flux<Product> findProducts(final SearchProducts searchProducts) {
        return productRepository
                .findByQuery(searchProducts.toDomain())
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Product> findById(final String productId) {
        return productRepository
                .findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product with ID %s was not found", productId)))
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Product> save(final CreateProduct createProduct) {
        return validator
                .validate(createProduct)
                .flatMap(create -> categoryRepository
                        .findById(create.getCategoryId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Category with ID %s was not found", create.getCategoryId())))
                        .map(category -> create.toDomain(idGenerator.newId(), category))
                )
                .flatMap(product -> productRepository.save(product).thenReturn(product))
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Void> updateById(final String productId, final UpdateProduct updateProduct) {
        return validator
                .validate(updateProduct)
                .flatMap(update -> productRepository.findById(productId))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product with ID %s was not found", productId)))
                .map(updateProduct::toDomain)
                .flatMap(productRepository::save)
                .then();
    }

    public Mono<Void> deleteById(final String productId) {
        return productRepository
                .deleteById(productId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product with ID %s was not found", productId))).then();
    }

    private Mono<Product> generatePreSignedUrlAndMapToDto(final com.raphaelcollin.inventorymanagement.domain.Product product) {
        return imageStorageClient
                .generatePreSignedUrlForVisualization(product.getImageIdentifier())
                .map(image -> Product.from(product, image));
    }
}
