package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raphaelcollin.inventorymanagement.domain.Product;
import com.raphaelcollin.inventorymanagement.domain.category.Category;
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
    String name;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 20, message = "the field must not exceed {max} characters")
    String sku;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 1000, message = "the field must not exceed {max} characters")
    String description;

    @NotNull(message = "the field is mandatory")
    @Positive(message = "the field must contain a positive value")
    BigDecimal price;

    @PositiveOrZero(message = "the field must contain a positive value")
    @NotNull(message = "the field is mandatory")
    Integer quantity;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 36, message = "the field must not exceed {max} characters")
    String imageIdentifier;

    @NotBlank(message = "the field is mandatory")
    @Size(max = 36, message = "the field must not exceed {max} characters")
    String categoryId;

    @JsonCreator
    public CreateProduct(@JsonProperty("name") final String name,
                         @JsonProperty("sku") final String sku,
                         @JsonProperty("description") final String description,
                         @JsonProperty("price") final BigDecimal price,
                         @JsonProperty("quantity") final Integer quantity,
                         @JsonProperty("imageIdentifier") final String imageIdentifier,
                        @JsonProperty("categoryId") final String categoryId) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageIdentifier = imageIdentifier;
        this.categoryId = categoryId;
    }

    public Product toDomain(final String productId, final Category category) {
        return Product
                .builder()
                .id(productId)
                .name(name)
                .sku(sku)
                .description(description)
                .price(price)
                .quantity(quantity)
                .imageIdentifier(imageIdentifier)
                .category(category)
                .build();
    }
}
