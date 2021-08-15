package com.raphaelcollin.inventorymanagement.infrastructure.rest.handler;

import com.raphaelcollin.inventorymanagement.api.CategoryService;
import com.raphaelcollin.inventorymanagement.api.dto.in.CreateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.in.UpdateCategory;
import com.raphaelcollin.inventorymanagement.api.dto.out.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class CategoryHandler {
    private final CategoryService categoryService;

    public Mono<ServerResponse> findAllCategories(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(categoryService.findAll(), Category.class);
    }

    public Mono<ServerResponse> saveCategory(ServerRequest request) {
        final Function<Category, Mono<ServerResponse>> responseBuilder = category -> ServerResponse
                .created(URI.create(request.path() + "/" + category.getId()))
                .bodyValue(category);

        return request.bodyToMono(CreateCategory.class)
                .flatMap(categoryService::save)
                .flatMap(responseBuilder);
    }

    public Mono<ServerResponse> updateCategoryById(ServerRequest request) {
        return request
                .bodyToMono(UpdateCategory.class)
                .flatMap(updateCategory -> categoryService.updateById(request.pathVariable("categoryId"), updateCategory))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteCategoryById(ServerRequest request) {
        return categoryService
                .deleteById(request.pathVariable("categoryId"))
                .then(ServerResponse.noContent().build());
    }
}

