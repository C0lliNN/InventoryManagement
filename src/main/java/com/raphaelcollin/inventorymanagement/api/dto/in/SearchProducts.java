package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import lombok.Value;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Value
public class SearchProducts {
    String name;
    Integer minQuantity;
    String categoryId;

    @JsonCreator
    public SearchProducts(@JsonProperty("name") String name,
                          @JsonProperty("minQuantity") Integer minQuantity,
                          @JsonProperty("categoryId") String categoryId) {
        this.name = name;
        this.minQuantity = minQuantity;
        this.categoryId = categoryId;
    }

    public ProductQuery toDomain() {
        return ProductQuery
                .builder()
                .name(name)
                .minQuantity(minQuantity)
                .categoryId(categoryId)
                .build();
    }

    public static SearchProducts fromQueryParams(MultiValueMap<String, String> queryParams) {
        return new SearchProducts(
                queryParams.getFirst("name"),
                Optional.ofNullable(queryParams.getFirst("minQuantity")).map(Integer::parseInt).orElse(null),
                queryParams.getFirst("categoryId")
        );
    }
}
