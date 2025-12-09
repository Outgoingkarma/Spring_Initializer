package com.example.spring_initializer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Driver extends User {
    private String licensePlate;
    @Enumerated(EnumType.STRING)
    private DriverVehicleType driverVehicleType;
    private boolean isAvailable;
    private int totalDeliveries;

    public Driver(String login, String password, String name, String surname, String phone_number, String email, boolean isAdmin, String licensePlate, DriverVehicleType driverVehicleType, boolean isAvailable, int totalDeliveries) {
        super(login, password, name, surname, phone_number, email, isAdmin);
        this.licensePlate = licensePlate;
        this.driverVehicleType = driverVehicleType;
        this.isAvailable = isAvailable;
        this.totalDeliveries = totalDeliveries;
    }
}














