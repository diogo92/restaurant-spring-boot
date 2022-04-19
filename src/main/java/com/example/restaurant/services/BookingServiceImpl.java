package com.example.restaurant.services;

import com.example.restaurant.exceptions.NoFreeTablesException;
import com.example.restaurant.model.Booking;
import com.example.restaurant.model.RestaurantTable;
import com.example.restaurant.repositories.BookingRepository;
import com.example.restaurant.repositories.TableRepository;
import com.example.restaurant.rules.BookingRulesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private BookingRulesFactory bookingRulesFactory;

    @Override
    public Booking bookTable(Booking bookingRequest) {

        bookingRulesFactory.setFact("bookingRequest", bookingRequest);
        bookingRulesFactory.fireRules();
        Booking booking = new Booking();
        List<Booking> bookingsAtTime = getBookingsAtTime(bookingRequest.getTime());

        List<RestaurantTable> tables = tableRepository.findAll();
        List<RestaurantTable> bookedTables = bookingsAtTime.stream().flatMap(b -> b.getTables().stream()).collect(Collectors.toList());
        tables.removeAll(bookedTables);
        if(tables.size() > 0) {
           List<RestaurantTable> tablesToBook = getTablesForBooking(bookingRequest.getNumOfPeople(), tables);
           booking.setTables(tablesToBook);
           booking.setTime(bookingRequest.getTime());
           booking.setNumOfPeople(bookingRequest.getNumOfPeople());
           bookingRepository.save(booking);
        }
        else {
            throw new NoFreeTablesException();
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsAtTime(LocalDateTime time) {

        LocalDateTime startDate = time.minusHours((long) 1.5);
        LocalDateTime endDate = time.plusHours((long) 1.5);

        return bookingRepository.findByTimeGreaterThanAndTimeLessThan(startDate, endDate);
    }

    @Override
    public List<Booking> getScheduledBookings() {
        LocalDateTime date = LocalDateTime.now();
        return bookingRepository.findByTimeGreaterThanEqual(date);
    }

    private RestaurantTable getTableWithPrefNumSeats(int numSeats, List<RestaurantTable> availableTables) {
        Optional<RestaurantTable> tab = availableTables.stream().filter(table -> table.getNumSeats() == numSeats).findAny();
        if (tab.isPresent()) {
            return tab.get();
        } else {
            tab = availableTables.stream().findAny();
            if(tab.isPresent()) {
                return tab.get();
            }
        }
        return null;
    }

    private RestaurantTable getTablesForNumPeople(int numPeople, List<RestaurantTable> availableTables) {
        RestaurantTable tab;
        if(numPeople <= 2){
            tab = getTableWithPrefNumSeats(2, availableTables);
        } else {// (numPeople <= 4)
            tab = getTableWithPrefNumSeats(4, availableTables);
        }
        if (tab == null) {
            throw new NoFreeTablesException();
        }
        return tab;
    }

    private List<RestaurantTable> getTablesForBooking(int numPeople, List<RestaurantTable> availableTables) {
        List<RestaurantTable> result = new ArrayList<>();
        while(numPeople > 2) {
            RestaurantTable tab = getTablesForNumPeople(4, availableTables);
            result.add(tab);
            availableTables.remove(tab);
            numPeople -= tab.getNumSeats();
        }
        if(numPeople > 0) {
            result.add(getTablesForNumPeople(2, availableTables));
        }
        return result;
    }
}
