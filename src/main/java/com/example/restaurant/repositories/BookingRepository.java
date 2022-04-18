package com.example.restaurant.repositories;

import com.example.restaurant.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTimeGreaterThanEqual(LocalDateTime time);
    List<Booking> findByTimeGreaterThanAndTimeLessThan(LocalDateTime startTime, LocalDateTime endTime);
}
