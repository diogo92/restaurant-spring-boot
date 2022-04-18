package com.example.restaurant.dto;

import com.example.restaurant.model.RestaurantTable;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingDTO {
    private Long id;
    private LocalDateTime time;
    private List<RestaurantTable> tables;
}
