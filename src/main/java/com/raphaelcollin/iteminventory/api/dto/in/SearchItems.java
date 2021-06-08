package com.raphaelcollin.iteminventory.api.dto.in;

import com.raphaelcollin.iteminventory.domain.ItemQuery;
import lombok.Value;

@Value
public class SearchItems {
    String title;
    int minQuantity;

    public ItemQuery toDomain() {
        return ItemQuery
                .builder()
                .title(title)
                .minQuantity(minQuantity)
                .build();
    }
}
