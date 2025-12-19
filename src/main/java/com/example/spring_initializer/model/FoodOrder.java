package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean isDelivered;
    private double orderPrice;
    @Enumerated(EnumType.STRING)
    private FoodOrderStatus orderStatus;
    private String deliveryAddress;


    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "food_order_dishes",
            joinColumns = @JoinColumn(name = "food_order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id"))
    private List<Dishes> dishes = new ArrayList<>();
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private BasicUser customer;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;







    public FoodOrder(int id, boolean isDelivered, double orderPrice, Chat chat, List<Dishes> dishes, BasicUser customer, Restaurant restaurant) {
        this.id = id;
        this.isDelivered = isDelivered;
        this.orderPrice = orderPrice;
        this.orderStatus = FoodOrderStatus.NEW;
        this.chat = chat;
        this.dishes = dishes;
        this.customer = customer;
        this.restaurant = restaurant;
    }

    public FoodOrder(boolean isDelivered, double orderPrice, Chat chat, List<Dishes> dishes, BasicUser customer, Restaurant restaurant) {
        this.isDelivered = isDelivered;
        this.orderPrice = orderPrice;
        this.orderStatus = FoodOrderStatus.NEW;
        this.chat = chat;
        this.dishes = dishes;
        this.customer = customer;
        this.restaurant = restaurant;
    }

    public FoodOrder() {
    }

    @JsonProperty("restaurantId")
    public Integer getRestaurantIdLite() {
        if (restaurant == null) return null;
        return Hibernate.isInitialized(restaurant) ? restaurant.getId() : null;
    }

    @JsonProperty("restaurantName")
    public String getRestaurantNameLite() {
        if (restaurant == null || !Hibernate.isInitialized(restaurant)) return null;
        return restaurant.getRestaurantName();
    }

    @JsonProperty("customerId")
    public Integer getCustomerIdLite() {
        if (customer == null) return null;
        return Hibernate.isInitialized(customer) ? customer.getId() : null;
    }

    @JsonProperty("customerName")
    public String getCustomerNameLite() {
        if (customer == null || !Hibernate.isInitialized(customer)) return null;

        String name = customer.getName() != null ? customer.getName() : "";
        String surname = customer.getSurname() != null ? customer.getSurname() : "";
        String full = (name + " " + surname).trim();
        return full.isEmpty() ? null : full;
    }

    @JsonProperty("items")
    public List<Map<String, Object>> getItemsLite() {
        if (dishes == null || !Hibernate.isInitialized(dishes)) return Collections.emptyList();

        List<Map<String, Object>> items = new ArrayList<>();
        for (Dishes d : dishes) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("name", d.getName());
            m.put("price", d.getPrice());
            items.add(m);
        }
        return items;
    }

    @JsonProperty("driverId")
    public Integer getDriverIdLite() {
        if (driver == null) return null;
        return Hibernate.isInitialized(driver) ? driver.getId() : null;
    }

}
