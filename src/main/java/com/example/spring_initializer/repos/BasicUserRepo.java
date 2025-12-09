package com.example.spring_initializer.repos;

import com.example.spring_initializer.model.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicUserRepo extends JpaRepository <BasicUser, Integer>{
}
