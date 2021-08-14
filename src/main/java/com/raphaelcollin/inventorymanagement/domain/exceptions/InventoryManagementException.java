package com.raphaelcollin.inventorymanagement.domain.exceptions;

public class InventoryManagementException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InventoryManagementException(final String message) {
        super(message);
    }
}
