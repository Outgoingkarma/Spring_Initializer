package com.example.spring_initializer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    public ChatMessage(String messageText, User messageSender, Chat chat) {
        this.messageText = messageText;
        this.messageSender = messageSender;
        this.chat = chat;
    }

    @JsonProperty("senderId")
    public Integer getSenderId() {
        return messageSender != null ? messageSender.getId() : null; // adjust getter name
    }

    @JsonProperty("senderLogin")
    public String getSenderLogin() {
        return messageSender != null ? messageSender.getLogin() : null; // adjust
    }

    @Override
    public String toString() {
        return dateCreated + "Message text: " + messageText + " " + messageSender;
    }
}
