package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.in.SearchProducts;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateProduct;
import com.raphaelcollin.inventorymanagement.api.dto.out.Product;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.ProductRepository;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
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

    private static final String CATEGORY_NOT_FOUND_FORMAT = "Category with ID %s was not found";
    private static final String PRODUCT_NOT_FOUND_FORMAT = "Product with ID %s was not found";

    public Flux<Product> findProducts(final SearchProducts searchProducts) {
        return productRepository
                .findByQuery(searchProducts.toDomain())
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Product> findById(final String productId) {
        return productRepository
                .findById(productId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(PRODUCT_NOT_FOUND_FORMAT, productId)))
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Product> save(final CreateProduct createProduct) {
        return validator
                .validate(createProduct)
                .flatMap(create -> findCategoryById(create.getCategoryId())
                        .map(category -> create.toDomain(idGenerator.newId(), category)))
                .flatMap(product -> productRepository.save(product).thenReturn(product))
                .flatMap(this::generatePreSignedUrlAndMapToDto);
    }

    public Mono<Void> updateById(final String productId, final UpdateProduct updateProduct) {
        return validator
                .validate(updateProduct)
                .flatMap(update -> productRepository.findById(productId))
                .switchIfEmpty(Mono.error(new EntityNotFoundException(PRODUCT_NOT_FOUND_FORMAT, productId)))
                .map(updateProduct::toDomain)
                .flatMap(product -> Mono.justOrEmpty(updateProduct.getCategoryId())
                        .flatMap(this::findCategoryById)
                        .map(product::withCategory).defaultIfEmpty(product))
                .flatMap(productRepository::save)
                .then();
    }

    public Mono<Void> deleteById(final String productId) {
        return productRepository
                .deleteById(productId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(PRODUCT_NOT_FOUND_FORMAT, productId))).then();
    }

    private Mono<Category> findCategoryById(final String categoryId) {
        return categoryRepository
                .findById(categoryId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(CATEGORY_NOT_FOUND_FORMAT, categoryId)));
    }

    private Mono<Product> generatePreSignedUrlAndMapToDto(final com.raphaelcollin.inventorymanagement.domain.Product product) {
        return imageStorageClient
                .generatePreSignedUrlForVisualization(product.getImageIdentifier())
                .map(image -> Product.from(product, image));
    }
}
