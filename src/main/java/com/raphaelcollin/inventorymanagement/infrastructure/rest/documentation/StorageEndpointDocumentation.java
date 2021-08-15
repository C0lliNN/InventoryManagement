package com.raphaelcollin.inventorymanagement.infrastructure.rest.documentation;

import com.raphaelcollin.inventorymanagement.api.StorageService;
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
                path = "/api/v1/storage",
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(operationId = "GeneratePreSignedUrl", summary = "Generate a Pre Signed Url for a Image Upload", tags = "Storage"),
                beanClass = StorageService.class, beanMethod = "generatePreSignedUrlForUpload"
        )
})
public @interface StorageEndpointDocumentation {
}
