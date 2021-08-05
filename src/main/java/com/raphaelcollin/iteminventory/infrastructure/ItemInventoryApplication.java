package com.raphaelcollin.iteminventory.infrastructure;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.raphaelcollin.iteminventory")
@OpenAPIDefinition(info = @Info(title = "Item Inventory", version = "1.0.0"))
public class ItemInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemInventoryApplication.class, args);
    }
}
