package com.raphaelcollin.iteminventory.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.iteminventory.domain.ItemQuery;
import lombok.Value;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Value
public class SearchItems {
    String title;
    Integer minQuantity;

    @JsonCreator
    public SearchItems(@JsonProperty("title") String title,
                       @JsonProperty("minQuantity") Integer minQuantity) {
        this.title = title;
        this.minQuantity = minQuantity;
    }

    public ItemQuery toDomain() {
        return ItemQuery
                .builder()
                .title(title)
                .minQuantity(minQuantity)
                .build();
    }

    public static SearchItems fromQueryParams(MultiValueMap<String, String> queryParams) {
        return new SearchItems(
                queryParams.getFirst("title"),
                Optional.ofNullable(queryParams.getFirst("minQuantity")).map(Integer::parseInt).orElse(null)
        );
    }
}
