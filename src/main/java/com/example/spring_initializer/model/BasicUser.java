package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BasicUser extends User {
    private String address;
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodOrder> myOrders;
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chat> chats;
    @JsonIgnore
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> myReviews;

    public BasicUser(String login, String password, String name, String surname, String phone_number, String email, String address) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
        this.isAdmin = false;
    }


    public void addOrder(FoodOrder foodOrder) {
        this.myOrders.add(foodOrder);
    }

    public void addReview(Review review) {
        this.myReviews.add(review);
    }

    public List<FoodOrder> getPendingOrders() {
        return myOrders.stream()
                .filter(order -> !order.isDelivered())
                .toList();
    }

    public List<FoodOrder> getCompletedOrders() {
        return myOrders.stream()
                .filter(FoodOrder::isDelivered)
                .toList();
    }

}
