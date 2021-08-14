package com.raphaelcollin.inventorymanagement.domain.category;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Category {
    @NonNull
    String id;
    @NonNull
    String name;
}
