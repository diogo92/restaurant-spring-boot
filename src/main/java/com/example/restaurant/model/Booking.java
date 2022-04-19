package com.example.restaurant.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Booking {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime time;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id")
    private List<RestaurantTable> tables;

    private int numOfPeople;

    public Booking () {}

    public Booking(LocalDateTime time, int numOfPeople) {
        this.time = time;
        this.numOfPeople = numOfPeople;
    }
}
