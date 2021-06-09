package com.raphaelcollin.iteminventory.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.iteminventory.domain.ItemQuery;
import lombok.Value;

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
}
