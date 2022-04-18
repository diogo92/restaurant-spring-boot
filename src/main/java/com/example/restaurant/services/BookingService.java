package com.example.restaurant.services;

import com.example.restaurant.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    public Booking bookTable(Booking bookingRequest);

    public List<Booking> getAllBookings();

    public List<Booking> getBookingsAtTime(LocalDateTime time);

    public List<Booking> getScheduledBookings();
}
