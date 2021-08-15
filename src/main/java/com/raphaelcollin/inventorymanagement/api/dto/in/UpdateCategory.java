package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
import lombok.Value;

import javax.validation.constraints.Size;
import java.util.Optional;

@Value
public class UpdateCategory {
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String name;

    @JsonCreator
    public UpdateCategory(@JsonProperty("name") final String name) {
        this.name = name;
    }

    public Category toDomain(final Category category) {
        final Category.CategoryBuilder builder = category.toBuilder();

        getName().map(builder::name);

        return builder.build();
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }
}
