package com.example.restaurant.exceptions;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(Long id) {
        super("Could not find table " + id);
    }
}
