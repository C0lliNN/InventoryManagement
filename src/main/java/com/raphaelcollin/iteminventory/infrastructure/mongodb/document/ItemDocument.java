package com.raphaelcollin.iteminventory.infrastructure.mongodb.document;

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
public class ItemDocument {

    @Id
    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private int quantity;
}
