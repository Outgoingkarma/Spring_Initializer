package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.FoodOrder;
import com.example.spring_initializer.model.FoodOrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodOrderRepo extends JpaRepository<FoodOrder, Integer> {
    List<FoodOrder> getFoodOrderByCustomerId(Integer id);
    @EntityGraph(attributePaths = {"restaurant", "customer"})
    List<FoodOrder> findByOrderStatus(FoodOrderStatus orderStatus);

    @EntityGraph(attributePaths = {"restaurant", "customer", "dishes"})
    Optional<FoodOrder> findWithDetailsById(Integer id);

    @EntityGraph(attributePaths = {"restaurant", "customer"})
    List<FoodOrder> findByOrderStatusAndDriver_Id(FoodOrderStatus status, Integer driverId);

    @EntityGraph(attributePaths = {"restaurant", "customer"})
    List<FoodOrder> findByDriver_Id(Integer driverId);

    @EntityGraph(attributePaths = {"restaurant", "customer"})
    List<FoodOrder> findByDriver_IdAndOrderStatus(Integer driverId, FoodOrderStatus status);

}
