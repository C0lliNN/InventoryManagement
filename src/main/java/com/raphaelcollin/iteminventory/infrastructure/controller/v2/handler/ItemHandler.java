package com.raphaelcollin.iteminventory.infrastructure.controller.v2.handler;

import com.raphaelcollin.iteminventory.api.ItemApi;
import com.raphaelcollin.iteminventory.api.dto.in.CreateItem;
import com.raphaelcollin.iteminventory.api.dto.in.SearchItems;
import com.raphaelcollin.iteminventory.api.dto.in.UpdateItem;
import com.raphaelcollin.iteminventory.api.dto.out.Item;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ItemHandler {
    private final ItemApi itemApi;

    public @NonNull Mono<ServerResponse> findAllItems(ServerRequest request) {
        final Function<SearchItems, Mono<ServerResponse>> responseBuilder = searchItems -> ServerResponse
                .ok()
                .body(itemApi.findItems(searchItems), Item.class);

        return Mono.just(request)
                .map(req -> SearchItems.fromQueryParams(req.queryParams()))
                .flatMap(responseBuilder);
    }

    public @NonNull Mono<ServerResponse> findItemById(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(itemApi.findById(request.pathVariable("itemId")), Item.class);
    }

    public @NonNull Mono<ServerResponse> saveItem(ServerRequest request) {
        final Function<Item, Mono<ServerResponse>> responseBuilder = item -> ServerResponse
                .created(URI.create("/api/v2/items/" + item.getId()))
                .bodyValue(item);

        return request
                .bodyToMono(CreateItem.class)
                .flatMap(itemApi::save)
                .flatMap(responseBuilder);
    }

    public @NonNull Mono<ServerResponse> updateItemById(ServerRequest request) {
        return request
                .bodyToMono(UpdateItem.class)
                .flatMap(updateItem -> itemApi.updateById(request.pathVariable("itemId"), updateItem))
                .then(ServerResponse.noContent().build());
    }

    public @NonNull Mono<ServerResponse> deleteItemById(ServerRequest request) {
        return Mono.just(request)
                .flatMap(req -> itemApi.deleteById(req.pathVariable("itemId")))
                .then(ServerResponse.noContent().build());
    }
}
