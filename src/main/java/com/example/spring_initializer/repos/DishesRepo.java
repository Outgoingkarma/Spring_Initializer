package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.Dishes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishesRepo extends JpaRepository<Dishes, Integer> {
}
