package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Integer> {
}
