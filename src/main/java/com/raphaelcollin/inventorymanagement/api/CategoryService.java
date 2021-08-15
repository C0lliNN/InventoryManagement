package com.raphaelcollin.inventorymanagement.api;

import com.raphaelcollin.inventorymanagement.api.dto.in.CreateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.out.Category;
import com.raphaelcollin.inventorymanagement.api.validation.RequestValidator;
import com.raphaelcollin.inventorymanagement.domain.category.CategoryRepository;
import com.raphaelcollin.inventorymanagement.domain.common.IdGenerator;
import com.raphaelcollin.inventorymanagement.domain.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final RequestValidator validator;
    private final IdGenerator idGenerator;

    public Flux<Category> findAll() {
        return repository
                .findAll()
                .map(Category::fromDomain);
    }

    public Mono<Category> save(final CreateCategory createCategory) {
        return validator
                .validate(createCategory)
                .map(create -> create.toDomain(idGenerator.newId()))
                .flatMap(category -> repository.save(category).thenReturn(category))
                .map(Category::fromDomain);
    }

    public Mono<Void> updateById(final String categoryId, final UpdateCategory updateCategory) {
        return validator
                .validate(updateCategory)
                .flatMap(update -> repository.findById(categoryId).map(update::toDomain))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Category with ID %s was not found", categoryId)))
                .flatMap(repository::save);
    }

    public Mono<Void> deleteById(final String categoryId) {
        return repository
                .deleteById(categoryId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Category with ID %s was not found", categoryId)))
                .then();
    }
}
