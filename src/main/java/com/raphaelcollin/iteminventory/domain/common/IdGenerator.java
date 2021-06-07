package com.raphaelcollin.iteminventory.domain.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {

    public String newId() {
        return UUID.randomUUID().toString();
    }
}
