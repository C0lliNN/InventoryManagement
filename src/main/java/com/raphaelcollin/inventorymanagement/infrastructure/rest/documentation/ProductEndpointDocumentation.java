package com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation;


import com.raphaelcollin.inventorymanagement.api.ProductApi;
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
                path = "/api/v1/products",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "FetchAllProducts", summary = "Fetch all stored Products", tags = "Products"),
                beanClass = ProductApi.class,
                beanMethod = "findProducts"
        ),
        @RouterOperation(
                path = "/api/v1/products/{productId}",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "FetchProductById", summary = "Fetch an Product by Id", tags = "Products"),
                beanClass = ProductApi.class,
                beanMethod = "findById"
        ),
        @RouterOperation(
                path = "/api/v1/products",
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "StoreProduct", summary = "Store an Product", tags = "Products"),
                beanClass = ProductApi.class, beanMethod = "save"
        ),
        @RouterOperation(
                path = "/api/v1/products/{productId}",
                method = RequestMethod.PATCH,
                operation = @Operation(operationId = "UpdateProduct", summary = "Update an existing Product", tags = "Products"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = ProductApi.class,
                beanMethod = "updateById"
        ),
        @RouterOperation(
                path = "/api/v1/products/{productId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "DeleteProduct", summary = "Delete an existing Product", tags = "Products"),
                beanClass = ProductApi.class,
                beanMethod = "deleteById"
        )
})
public @interface ProductEndpointDocumentation {
}
