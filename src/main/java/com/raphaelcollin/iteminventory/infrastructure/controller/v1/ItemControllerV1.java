package com.raphaelcollin.iteminventory.infrastructure.controller.v1;

import com.raphaelcollin.iteminventory.api.ItemApi;
import com.raphaelcollin.iteminventory.api.dto.in.CreateItem;
import com.raphaelcollin.iteminventory.api.dto.in.SearchItems;
import com.raphaelcollin.iteminventory.api.dto.in.UpdateItem;
import com.raphaelcollin.iteminventory.api.dto.out.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/items")
@AllArgsConstructor
public class ItemControllerV1 {
    private final ItemApi itemApi;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Item> findAllItems(SearchItems searchItems) {
        return itemApi.findItems(searchItems);
    }

    @GetMapping(value = "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Item> findItemById(@PathVariable("itemId") String itemId) {
        return itemApi.findById(itemId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> saveItem(@Valid @RequestBody CreateItem createItem) {
        return itemApi.save(createItem);
    }

    @PatchMapping(value = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateItemById(@PathVariable("itemId") String itemId, @RequestBody @Valid UpdateItem updateItem) {
        return itemApi.updateById(itemId, updateItem);
    }

    @DeleteMapping(value = "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteItemById(@PathVariable("itemId") String itemId) {
        return itemApi.deleteById(itemId);
    }
}
