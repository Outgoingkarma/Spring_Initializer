package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime lastMessageDate = LocalDateTime.now();



    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "chat")
    private FoodOrder order;
    @JsonIgnore
    @ManyToOne
    private BasicUser customer;
    @JsonIgnore
    @ManyToOne
    private Driver driver;
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;
    @JsonIgnore
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
}
