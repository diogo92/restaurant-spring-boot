package com.example.restaurant.exceptions;

public class NoFreeTablesException extends RuntimeException{
    public NoFreeTablesException() {
        super("No free tables available");
    }
}