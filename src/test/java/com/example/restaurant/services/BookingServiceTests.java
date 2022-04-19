package com.example.restaurant.services;

import com.example.restaurant.exceptions.NoFreeTablesException;
import com.example.restaurant.exceptions.service.BookingServiceException;
import com.example.restaurant.model.Booking;
import com.example.restaurant.repositories.BookingRepository;
import com.example.restaurant.repositories.TableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingServiceTests {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TableRepository tableRepository;

    @Test
    void rulesExceptionBookings() {

        bookingRepository.deleteAll();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        assertThrows(BookingServiceException.class,() -> addBookingIteratively(1,0,localDateTime));
        assertThrows(BookingServiceException.class,() -> addBookingIteratively(1,9,localDateTime));
    }

    @Test
    void addBookings() {

        bookingRepository.deleteAll();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        addBookingIteratively(4,2,localDateTime);
        addBookingIteratively(4,4,localDateTime);

        assertEquals(8,bookingRepository.findAll().size());
    }

    @Test
    void exceedCapacityBooking() {

        bookingRepository.deleteAll();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        addBookingIteratively(4,2,localDateTime);
        addBookingIteratively(4,3,localDateTime);

        assertThrows(NoFreeTablesException.class, () -> bookingService.bookTable (new Booking(localDateTime, 2)));
    }

    @Test
    void addBookingWithMoreThanFourPeople() {

        bookingRepository.deleteAll();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        bookingService.bookTable( new Booking(localDateTime, 6));
        bookingService.bookTable( new Booking(localDateTime, 7));

        assertEquals(2,bookingRepository.findAll().size());
    }

    @Test
    void exceedBookingCapacityWithOneFreeTable() {

        bookingRepository.deleteAll();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        bookingService.bookTable(new Booking(localDateTime, 6));
        bookingService.bookTable(new Booking(localDateTime, 7));
        bookingService.bookTable(new Booking(localDateTime, 7));
        assertThrows(NoFreeTablesException.class, () -> bookingService.bookTable(new Booking(localDateTime, 6)));
    }

    @Test
    void addBookingsDifferentHours() {

        bookingRepository.deleteAll();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-12 13:30:00", df);

        bookingService.bookTable(new Booking(localDateTime, 6));
        bookingService.bookTable(new Booking(localDateTime, 7));
        bookingService.bookTable(new Booking(localDateTime, 7));
        bookingService.bookTable(new Booking(localDateTime.plusHours((long) 1.5), 6));
        bookingService.bookTable(new Booking(localDateTime.plusHours((long) 1.5), 7));
        bookingService.bookTable(new Booking(localDateTime.plusHours((long) 1.5), 7));
        bookingService.bookTable(new Booking(localDateTime.plusHours((long) 4.5), 7));
        bookingService.bookTable(new Booking(localDateTime.plusHours((long) 4.5), 7));
        bookingService.bookTable(new Booking(localDateTime.plusHours(3), 7));


        assertEquals(9,bookingRepository.findAll().size());
    }

    private void addBookingIteratively(int amount, int numPeople, LocalDateTime date) {
        for(int i = 0; i < amount; i++) {
            bookingService.bookTable(new Booking(date, numPeople));
        }
    }

}
