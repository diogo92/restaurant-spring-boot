package com.example.restaurant.exceptions.service;

public class BookingServiceException extends RuntimeException{
    public BookingServiceException(String message) {
        super("BookingService exception:\n" + message);
    }
}
