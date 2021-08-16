package com.raphaelcollin.inventorymanagement.infrastructure.mongodb.document;

import com.raphaelcollin.inventorymanagement.domain.category.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocument {

    @Id
    private String id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageIdentifier;
    private CategoryDocument category;
}
