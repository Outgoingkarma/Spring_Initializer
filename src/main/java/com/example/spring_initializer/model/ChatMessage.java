package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private String messageText;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private boolean isRead = false;

    @JsonIgnore
    @ManyToOne
    private User messageSender;
    @JsonIgnore
    @ManyToOne
    private Chat chat;

}
