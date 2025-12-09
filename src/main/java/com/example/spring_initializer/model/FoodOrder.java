package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    private BasicUser customer;
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;
}
