package com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation;

import com.raphaelcollin.inventorymanagement.api.CategoryService;
import com.raphaelcollin.inventorymanagement.api.ProductService;
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
                path = "/api/v1/categories",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "FetchAllCategories", summary = "Fetch all stored Categories", tags = "Categories"),
                beanClass = CategoryService.class,
                beanMethod = "findAll"
        ),
        @RouterOperation(
                path = "/api/v1/categories",
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "StoreCategory", summary = "Store an Category", tags = "Categories"),
                beanClass = CategoryService.class, beanMethod = "save"
        ),
        @RouterOperation(
                path = "/api/v1/categories/{categoryId}",
                method = RequestMethod.PATCH,
                operation = @Operation(operationId = "UpdateCategory", summary = "Update an existing Category", tags = "Categories"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = CategoryService.class,
                beanMethod = "updateById"
        ),
        @RouterOperation(
                path = "/api/v1/categories/{categoryId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "DeleteCategory", summary = "Delete an existing Category", tags = "Categories"),
                beanClass = ProductService.class,
                beanMethod = "deleteById"
        )
})
public @interface CategoryEndpointDocumentation {
}
