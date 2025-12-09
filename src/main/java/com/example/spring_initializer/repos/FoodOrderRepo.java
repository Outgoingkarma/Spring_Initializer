package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodOrderRepo extends JpaRepository<FoodOrder, Integer> {
}
