package com.example.restaurant.controllers;

import com.example.restaurant.dto.BookingDTO;
import com.example.restaurant.model.Booking;
import com.example.restaurant.services.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookingService")
public class BookingController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookingService service;

    @GetMapping("/bookings")
    List<BookingDTO> getAllBookings() {
        return service.getAllBookings().stream().map(booking -> modelMapper.map(booking, BookingDTO.class)).collect(Collectors.toList());
    }

    @GetMapping("/bookings/scheduled")
    List<BookingDTO> getScheduledBookings() {
        return  service.getScheduledBookings().stream().map(booking -> modelMapper.map(booking, BookingDTO.class)).collect(Collectors.toList());
    }

    @PostMapping("/bookings/addBooking")
    public ResponseEntity<BookingDTO> bookTable(@RequestBody BookingDTO bookingDto) {
        // convert DTO to entity
        Booking bookingRequest = modelMapper.map(bookingDto, Booking.class);
        Booking booking = service.bookTable(bookingRequest);

        // convert entity to DTO
        BookingDTO bookingResponse = modelMapper.map(booking, BookingDTO.class);

        return new ResponseEntity<BookingDTO>(bookingResponse, HttpStatus.CREATED);
    }
}
