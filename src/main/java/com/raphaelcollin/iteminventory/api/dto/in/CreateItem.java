package com.raphaelcollin.iteminventory.api.dto.in;

import com.raphaelcollin.iteminventory.domain.Item;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Value
public class CreateItem {
    @NotBlank(message = "the field is mandatory")
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String title;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 1000, message = "the field must not exceed {max} characters")
    String description;

    @NotNull(message = "the field is mandatory")
    @Positive(message = "the field must contain a positive value")
    BigDecimal price;

    @Positive(message = "the field must contain a positive value")
    @NotNull(message = "the field is mandatory")
    Integer quantity;

    public Item toDomain(final String itemId) {
        return Item
                .builder()
                .id(itemId)
                .title(title)
                .description(description)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
