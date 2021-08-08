package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.Product;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Value
public class CreateProduct {
    @NotBlank(message = "the field is mandatory")
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String title;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 1000, message = "the field must not exceed {max} characters")
    String description;

    @NotNull(message = "the field is mandatory")
    @Positive(message = "the field must contain a positive value")
    BigDecimal price;

    @PositiveOrZero(message = "the field must contain a positive value")
    @NotNull(message = "the field is mandatory")
    Integer quantity;

    public Product toDomain(final String productId) {
        return Product
                .builder()
                .id(productId)
                .title(title)
                .description(description)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
