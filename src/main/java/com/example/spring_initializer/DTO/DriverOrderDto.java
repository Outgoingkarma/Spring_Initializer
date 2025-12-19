package com.example.spring_initializer.DTO;

import com.example.spring_initializer.model.FoodOrderStatus;

public class DriverOrderDto {
    public int id;
    public double orderPrice;
    public FoodOrderStatus orderStatus;
    public String restaurantName;
    public String deliveryAddress;

    public DriverOrderDto(int id, double orderPrice, FoodOrderStatus orderStatus, String restaurantName, String deliveryAddress) {
        this.id = id;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
        this.restaurantName = restaurantName;
        this.deliveryAddress = deliveryAddress;
    }
    public static class AcceptOrderRequest {
        public int orderId;
        public int driverId;
    }
}