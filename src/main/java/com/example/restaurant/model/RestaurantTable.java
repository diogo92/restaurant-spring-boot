package com.example.restaurant.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RestaurantTable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private int numSeats;

    public RestaurantTable() {}

    public RestaurantTable(int numSeats) {
        this.numSeats=numSeats;
    }

}
