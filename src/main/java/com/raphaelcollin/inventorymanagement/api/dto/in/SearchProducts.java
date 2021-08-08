package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.inventorymanagement.domain.ProductQuery;
import lombok.Value;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Value
public class SearchProducts {
    String title;
    Integer minQuantity;

    @JsonCreator
    public SearchProducts(@JsonProperty("title") String title,
                          @JsonProperty("minQuantity") Integer minQuantity) {
        this.title = title;
        this.minQuantity = minQuantity;
    }

    public ProductQuery toDomain() {
        return ProductQuery
                .builder()
                .title(title)
                .minQuantity(minQuantity)
                .build();
    }

    public static SearchProducts fromQueryParams(MultiValueMap<String, String> queryParams) {
        return new SearchProducts(
                queryParams.getFirst("title"),
                Optional.ofNullable(queryParams.getFirst("minQuantity")).map(Integer::parseInt).orElse(null)
        );
    }
}
