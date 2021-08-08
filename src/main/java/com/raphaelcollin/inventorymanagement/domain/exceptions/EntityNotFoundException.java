package com.raphaelcollin.inventorymanagement.domain.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(final String message) {
        super(message);
    }
}
