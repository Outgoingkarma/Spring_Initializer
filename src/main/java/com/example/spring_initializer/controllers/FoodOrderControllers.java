package com.example.spring_initializer.controllers;

import com.example.spring_initializer.DTO.DriverOrderDto;
import com.example.spring_initializer.model.*;
import com.example.spring_initializer.repos.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RestController
public class FoodOrderControllers {
    @Autowired
    private FoodOrderRepo foodOrderRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;
    @Autowired
    private DishesRepo dishesRepo;
    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;
    @Autowired
    private ChatMessageRepo chatMessageRepo;
    @Autowired
    private DriverRepo driverRepo;


    @GetMapping("getAllFoodOrders")
    public @ResponseBody Iterable<FoodOrder> getAllFoodOrders(){
        return foodOrderRepo.findAll();}

    @GetMapping("getMenuRestaurant/{id}")
    public @ResponseBody Iterable<Dishes> getMenuRestaurant(@PathVariable Integer id){
        return dishesRepo.getDishesByRestaurantId(id);}

    @GetMapping("getOrderByUser/{id}")
    public @ResponseBody Iterable<FoodOrder> getOrderForUser(@PathVariable Integer id){
        return foodOrderRepo.getFoodOrderByCustomerId(id);}

    @GetMapping("getOrderById/{id}")
    @Transactional
    public @ResponseBody FoodOrder getOrderById(@PathVariable Integer id){
        return foodOrderRepo.findWithDetailsById(id).orElse(null);
    }



    @GetMapping("getMessagesForOrder/{id}")
    public @ResponseBody Iterable<ChatMessage> getMessageForOrder(@PathVariable Integer id){
        Chat chat = chatRepo.getChatByOrderId(id);
        if(chat == null){
            FoodOrder foodOrder = foodOrderRepo.getReferenceById(id);
            Chat newChat = new Chat("User" + foodOrder.getCustomer().getLogin(), foodOrder);
            foodOrder.setChat(newChat);
            chatRepo.save(newChat);
            foodOrderRepo.save(foodOrder);
        }
        Chat existingChat = chatRepo.getChatByOrderId(id);
        return existingChat != null ? existingChat.getMessages() : new ArrayList<>();
    }

    @PostMapping("sendMessage")
    public @ResponseBody String sendMessage(@RequestBody String message){
        Gson gson = new Gson();
        Properties properties = gson.fromJson(message, Properties.class);
        var messageText = properties.getProperty("messageText");
        var messageOwner = basicUserRepo.getReferenceById(Integer.parseInt(properties.getProperty("userId")));
        var foodOrder = foodOrderRepo.getReferenceById(Integer.valueOf(properties.getProperty("orderId")));

        Chat chat = chatRepo.getChatByOrderId(foodOrder.getId());
        if (chat == null){
            chat = new Chat("User" + messageOwner.getLogin(), foodOrder);
            foodOrder.setChat(chat);
            chatRepo.save(chat);
            foodOrderRepo.save(foodOrder);
        }
        ChatMessage chatMessage = new ChatMessage(messageText, messageOwner, chat);
        chatMessageRepo.save(chatMessage);
        return "Message sent";
    }
    @PutMapping("updateOrderStatus")
    public @ResponseBody FoodOrder updateOrderStatus(@RequestBody FoodOrder foodOrder){
        FoodOrder existingOrder = foodOrderRepo.getReferenceById(foodOrder.getId());
        if(existingOrder != null){
            existingOrder.setOrderStatus(foodOrder.getOrderStatus());
            return foodOrderRepo.save(existingOrder);
        }
        return null;
    }

    @PostMapping("createOrder")
    public @ResponseBody FoodOrder createOrder(@RequestBody String info){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(info, JsonObject.class);
        Integer userId = jsonObject.get("userId").getAsInt();
        Integer restaurantId = jsonObject.get("restaurantId").getAsInt();
        BasicUser customer = basicUserRepo.getReferenceById(userId);
        Restaurant restaurant = restaurantRepo.getReferenceById(restaurantId);
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        List<Dishes> dishes = new ArrayList<>();
        double totalPrice = 0;
        for(int i = 0; i < itemsArray.size(); i++){
            JsonObject item = itemsArray.get(i).getAsJsonObject();
            int dishId = item.get("dishesId").getAsInt();
            int quantity = item.get("quantity").getAsInt();
            Dishes dish = dishesRepo.getReferenceById(dishId);
            for (int q = 0; q < quantity; q++) {
                dishes.add(dish);
                totalPrice += dish.getPrice();
            }
        }

        FoodOrder foodOrder = new FoodOrder(false, totalPrice, null, dishes, customer, restaurant);
        foodOrder.setDeliveryAddress(customer.getAddress());
        return foodOrderRepo.save(foodOrder);
    }

    @GetMapping("getNewOrders")
    public @ResponseBody Iterable<FoodOrder> getNewOrders() {
        return foodOrderRepo.findByOrderStatus(FoodOrderStatus.NEW);
    }

    @GetMapping("getNewOrdersLite")
    @Transactional
    public List<DriverOrderDto> getNewOrdersLite() {
        List<FoodOrder> orders = foodOrderRepo.findByOrderStatus(FoodOrderStatus.NEW);

        List<DriverOrderDto> out = new ArrayList<>();
        for (FoodOrder o : orders) {
            String restName = (o.getRestaurant() != null) ? o.getRestaurant().getRestaurantName() : null;
            String addr = o.getDeliveryAddress();

            out.add(new DriverOrderDto(
                    o.getId(),
                    o.getOrderPrice(),
                    o.getOrderStatus(),
                    restName,
                    addr
            ));
        }
        return out;
    }



    @GetMapping("getDriverInProgressLite/{driverId}")
    @Transactional
    public List<DriverOrderDto> getDriverInProgressLite(@PathVariable Integer driverId) {
        List<FoodOrder> orders = foodOrderRepo.findByOrderStatusAndDriver_Id(FoodOrderStatus.IN_PROGRESS, driverId);

        List<DriverOrderDto> out = new ArrayList<>();
        for (FoodOrder o : orders) {
            String restName = (o.getRestaurant() != null) ? o.getRestaurant().getRestaurantName() : null;
            out.add(new DriverOrderDto(o.getId(), o.getOrderPrice(), o.getOrderStatus(), restName, o.getDeliveryAddress()));
        }
        return out;
    }


    @PutMapping("driverAcceptOrder")
    @Transactional
    public FoodOrder driverAcceptOrder(@RequestBody DriverOrderDto.AcceptOrderRequest req) {

        FoodOrder o = foodOrderRepo.findById(req.orderId).orElseThrow();
        Driver d = driverRepo.findById(req.driverId).orElseThrow();
        if (o.getOrderStatus() != FoodOrderStatus.NEW) {
            throw new RuntimeException("Order is not NEW");
        }
        if (o.getDriver() != null) throw new RuntimeException("Order already assigned");
        o.setDriver(d);
        o.setOrderStatus(FoodOrderStatus.IN_PROGRESS);
        return foodOrderRepo.save(o);
    }

    public static class FinishOrderRequest {
        public int orderId;
        public int driverId;
    }

    @PutMapping("driverFinishOrder")
    @Transactional
    public FoodOrder driverFinishOrder(@RequestBody FinishOrderRequest req) {
        FoodOrder o = foodOrderRepo.findById(req.orderId).orElseThrow();

        if (o.getOrderStatus() != FoodOrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Order is not IN_PROGRESS");
        }
        if (o.getDriver() == null || o.getDriver().getId() != req.driverId) {
            throw new RuntimeException("Order not assigned to this driver");
        }

        o.setOrderStatus(FoodOrderStatus.FINISHED);
        o.setDelivered(true);
        return foodOrderRepo.save(o);
    }

    @GetMapping("getOrdersByDriver/{driverId}")
    @Transactional
    public List<FoodOrder> getOrdersByDriver(@PathVariable Integer driverId) {
        return foodOrderRepo.findByDriver_Id(driverId);
        // or only finished:
        // return foodOrderRepo.findByDriver_IdAndOrderStatus(driverId, FoodOrderStatus.FINISHED);
    }


}
