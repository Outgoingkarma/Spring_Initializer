package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<Chat, Integer> {
}
