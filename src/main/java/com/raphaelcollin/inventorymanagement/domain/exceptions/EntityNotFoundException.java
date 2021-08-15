package com.raphaelcollin.inventorymanagement.domain.exceptions;

public class EntityNotFoundException extends InventoryManagementException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(final String message, Object... args) {
        super(message, args);
    }
}
