package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
}
