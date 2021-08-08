package com.raphaelcollin.inventorymanagement.api.dto.in;

import com.raphaelcollin.inventorymanagement.domain.Item;
import lombok.Value;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Optional;

@Value
public class UpdateItem {
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String title;

    @Size(max = 1000, message = "the field must not exceed {max} characters")
    String description;

    @Positive(message = "the field must contain a positive value")
    BigDecimal price;

    @PositiveOrZero(message = "the field must contain a positive value")
    Integer quantity;

    public Item toDomain(final Item item) {
        final Item.ItemBuilder builder = item.toBuilder();

        getTitle().map(builder::title);
        getDescription().map(builder::description);
        getPrice().map(builder::price);
        getQuantity().map(builder::quantity);

        return builder.build();
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    public Optional<Integer> getQuantity() {
        return Optional.ofNullable(quantity);
    }
}
