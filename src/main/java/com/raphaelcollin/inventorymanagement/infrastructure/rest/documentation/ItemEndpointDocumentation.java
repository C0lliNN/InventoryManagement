package com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation;


import com.raphaelcollin.inventorymanagement.api.ItemApi;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = "/api/v1/items",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "FetchAllItems", summary = "Fetch all stored Items", tags = "Items"),
                beanClass = ItemApi.class,
                beanMethod = "findItems"
        ),
        @RouterOperation(
                path = "/api/v1/items/{itemId}",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "FetchItemById", summary = "Fetch an Item by Id", tags = "Items"),
                beanClass = ItemApi.class,
                beanMethod = "findById"
        ),
        @RouterOperation(
                path = "/api/v1/items",
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "StoreItem", summary = "Store an Item", tags = "Items"),
                beanClass = ItemApi.class, beanMethod = "save"
        ),
        @RouterOperation(
                path = "/api/v1/items/{itemId}",
                method = RequestMethod.PATCH,
                operation = @Operation(operationId = "UpdateItem", summary = "Update an existing Item", tags = "Items"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = ItemApi.class,
                beanMethod = "updateById"
        ),
        @RouterOperation(
                path = "/api/v1/items/{itemId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "DeleteItem", summary = "Delete an existing Item", tags = "Items"),
                beanClass = ItemApi.class,
                beanMethod = "deleteById"
        )
})
public @interface ItemEndpointDocumentation {
}
