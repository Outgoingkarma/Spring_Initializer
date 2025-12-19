package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    //User getUserByLoginAndPassword(String login, String password);
    User getUserByLogin(String login);
}
