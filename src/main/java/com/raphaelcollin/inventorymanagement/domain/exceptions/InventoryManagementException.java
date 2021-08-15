package com.raphaelcollin.inventorymanagement.domain.exceptions;

import static java.lang.String.format;

public class InventoryManagementException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InventoryManagementException(final String message, Object... args) {
        super(format(message, args));
    }
}
