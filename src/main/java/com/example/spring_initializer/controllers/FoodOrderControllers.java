package com.example.spring_initializer.controllers;

import com.example.spring_initializer.model.Dishes;
import com.example.spring_initializer.model.FoodOrder;
import com.example.spring_initializer.repos.DishesRepo;
import com.example.spring_initializer.repos.FoodOrderRepo;
import com.example.spring_initializer.repos.RestaurantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FoodOrderControllers {
    @Autowired
    private FoodOrderRepo foodOrderRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;
    @Autowired
    private DishesRepo dishesRepo;


    @GetMapping("getAllFoodOrders")
    public @ResponseBody Iterable<FoodOrder> getAllFoodOrders(){return foodOrderRepo.findAll();}
    @GetMapping("getRestaurantMenu/{id}")
    public @ResponseBody Iterable<Dishes> getRestaurantMenu(@PathVariable Integer id){return dishesRepo.getDishesByRestaurantId(id);}
}
