package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class CreateCategory {
    @NotBlank(message = "the field is mandatory")
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String name;

    public Category toDomain(final String id) {
        return Category
                .builder()
                .id(id)
                .name(name)
                .build();
    }
}
