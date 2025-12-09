package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {
}
