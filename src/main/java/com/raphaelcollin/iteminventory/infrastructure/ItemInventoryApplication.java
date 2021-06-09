package com.raphaelcollin.iteminventory.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.raphaelcollin.iteminventory")
public class ItemInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemInventoryApplication.class, args);
    }
}
