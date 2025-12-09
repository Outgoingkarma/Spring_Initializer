package com.example.spring_initializer.controllers;

import ch.qos.logback.core.net.server.Client;
import com.example.spring_initializer.model.BasicUser;
import com.example.spring_initializer.model.Driver;
import com.example.spring_initializer.model.User;
import com.example.spring_initializer.repos.*;
import com.google.gson.JsonObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import java.util.Properties;

@RestController
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;



    @GetMapping(value = "/allUsers")
    public @ResponseBody
    Iterable<User> getAll(){
        return userRepo.findAll();
    }


//    @GetMapping(value = "validateClient")
//    public User validateClient(@RequestParam String login, @RequestParam String password){
//        return userRepo.getUserByLoginAndPassword(login, password);
//    }

    @PostMapping(value = "validateUser") //http://localhost:8080/validateUser
    public @ResponseBody String getUserByCredentials(@RequestBody String info) {
        System.out.println(info);
        //?Kaip parsint
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var login = properties.getProperty("login");
        var psw = properties.getProperty("password");
        User user = userRepo.getUserByLoginAndPassword(login, psw);
        if (user != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userType", user.getClass().getName());
            jsonObject.addProperty("login", user.getLogin());
            jsonObject.addProperty("password", user.getPassword());
            jsonObject.addProperty("name", user.getName());
            jsonObject.addProperty("surname", user.getSurname());
            jsonObject.addProperty("id", user.getId());

            String json = gson.toJson(jsonObject);

            return json;
        }
        return null;
    }

    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PutMapping(value = "updateUserById/{id}")
    public @ResponseBody User updateUserById(@RequestBody String info, @PathVariable int id) {

        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException()); //cia noriu savo custom error

        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var name = properties.getProperty("name");
        user.setName(name);

        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PostMapping(value = "insertBasic")
    public @ResponseBody BasicUser insertBasicUser(@RequestBody BasicUser basicUser){
        basicUser.setDateCreated(java.time.LocalDateTime.now());
        basicUser.setDateModified(java.time.LocalDateTime.now());
        basicUser.setAdmin(false);
        return basicUserRepo.save(basicUser);
    }

    @PostMapping(value = "insertDriver")
    public @ResponseBody Driver insertDriver(@RequestBody Driver driver){
        driver.setDateCreated(java.time.LocalDateTime.now());
        driver.setDateModified(java.time.LocalDateTime.now());
        driver.setAdmin(false);
        return driverRepo.save(driver);
    }





}
