package com.example.restaurant.services;

import com.example.restaurant.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking bookTable(Booking bookingRequest);

    List<Booking> getAllBookings();

    List<Booking> getBookingsAtTime(LocalDateTime time);

    List<Booking> getScheduledBookings();
}
