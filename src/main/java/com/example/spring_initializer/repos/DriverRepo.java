package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepo extends JpaRepository<Driver, Integer> {
}
