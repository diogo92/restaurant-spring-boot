package com.example.restaurant.loader;

import com.example.restaurant.model.RestaurantTable;
import com.example.restaurant.repositories.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableDatabaseLoader implements CommandLineRunner {

    @Autowired
    private TableRepository repository;

    @Override
    public void run(String... strings) throws Exception {

        for(int i = 0; i < 4; i++) {
            this.repository.save(new RestaurantTable(2));
        }
        for(int i = 0; i < 4; i++) {
            this.repository.save(new RestaurantTable(4));
        }
    }
}