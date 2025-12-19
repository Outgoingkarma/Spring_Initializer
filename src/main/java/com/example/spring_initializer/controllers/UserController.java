package com.example.spring_initializer.controllers;

import com.example.spring_initializer.model.BasicUser;
import com.example.spring_initializer.model.Driver;
import com.example.spring_initializer.model.Restaurant;
import com.example.spring_initializer.model.User;
import com.example.spring_initializer.repos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import java.util.Properties;
import org.springframework.http.ResponseEntity;

@RestController
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BCryptPasswordEncoder encoder;


    @GetMapping(value = "/allUsers")
    public @ResponseBody
    Iterable<User> getAll(){
        return userRepo.findAll();
    }


//    @GetMapping(value = "validateClient")
//    public User validateClient(@RequestParam String login, @RequestParam String password){
//        return userRepo.getUserByLoginAndPassword(login, password);
//    }



    @PostMapping("validateUser")
    public ResponseEntity<String> validateUser(@RequestBody String info) {
        Gson gson = new Gson();
        Properties props = gson.fromJson(info, Properties.class);

        String login = props.getProperty("login");
        String psw   = props.getProperty("password");

        User user = userRepo.getUserByLogin(login);
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        String stored = user.getPassword();
        boolean ok = stored != null && stored.startsWith("$2")
                ? encoder.matches(psw, stored)
                : psw != null && psw.equals(stored);

        if (!ok) return ResponseEntity.status(401).body("Invalid credentials");

        // upgrade plaintext -> bcrypt
        if (stored != null && !stored.startsWith("$2")) {
            user.setPassword(encoder.encode(psw));
            userRepo.save(user);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userType", user.getClass().getName());
        jsonObject.addProperty("login", user.getLogin());
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("surname", user.getSurname());
        jsonObject.addProperty("id", user.getId());

        return ResponseEntity.ok(gson.toJson(jsonObject));
    }

//    @PutMapping(value = "updateUser")
//    public @ResponseBody User updateUser(@RequestBody User user) {
//        userRepo.save(user);
//        return userRepo.getReferenceById(user.getId());
//    }

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
        basicUser.setPassword(encoder.encode(basicUser.getPassword()));

        return basicUserRepo.save(basicUser);
    }

    @PostMapping(value = "insertDriver")
    public @ResponseBody Driver insertDriver(@RequestBody Driver driver){
        driver.setDateCreated(java.time.LocalDateTime.now());
        driver.setDateModified(java.time.LocalDateTime.now());
        driver.setAdmin(false);
        driver.setPassword(encoder.encode(driver.getPassword()));

        return driverRepo.save(driver);
    }

    @GetMapping(value = "/allRestaurants")
    public @ResponseBody Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }
    @GetMapping(value = "/allDrivers")
    public @ResponseBody Iterable<Driver> getAllDrivers() {
        return driverRepo.findAll();
    }
    @GetMapping(value = "/allBasicUsers")
    public @ResponseBody Iterable<BasicUser> getAllBasicUsers() {
        return basicUserRepo.findAll();
    }

    @DeleteMapping(value = "deleteUser/{id}")
    public @ResponseBody String deleteUser(@PathVariable Integer id) {
        userRepo.deleteById(id);
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            return "failed to delete user";
        } else {
            return "successfully deleted user";
        }
    }

    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody String userJson) {
        try {
            // Use Jackson ObjectMapper which handles LocalDate properly
            JsonNode jsonNode = objectMapper.readTree(userJson);

            if (!jsonNode.has("id")) {
                throw new RuntimeException("User ID is required for update");
            }

            int userId = jsonNode.get("id").asInt();

            // First, fetch the existing user to determine its actual type
            User existingUser = userRepo.findById(userId).orElse(null);
            if (existingUser == null) {
                throw new RuntimeException("User with ID " + userId + " not found");
            }

            // Update based on the actual type in the database
            if (existingUser instanceof Driver) {
                // It's a Driver - fetch from driver repo and update
                Driver existingDriver = driverRepo.findById(userId).orElse(null);
                if (existingDriver == null) {
                    throw new RuntimeException("Driver with ID " + userId + " not found");
                }

                // Deserialize the incoming data
                Driver updatedDriver = objectMapper.readValue(userJson, Driver.class);

                // Update fields
                existingDriver.setLogin(updatedDriver.getLogin());
                if (updatedDriver.getPassword() != null && !updatedDriver.getPassword().isBlank()) {
                    existingDriver.setPassword(encoder.encode(updatedDriver.getPassword()));
                }
                existingDriver.setName(updatedDriver.getName());
                existingDriver.setSurname(updatedDriver.getSurname());
                existingDriver.setPhone_number(updatedDriver.getPhone_number());
                existingDriver.setLicensePlate(updatedDriver.getLicensePlate());
                existingDriver.setDriverVehicleType(updatedDriver.getDriverVehicleType());
                existingDriver.setDateModified(java.time.LocalDateTime.now());
                existingDriver.setTotalDeliveries(updatedDriver.getTotalDeliveries());
                existingDriver.setAvailable(updatedDriver.isAvailable());
                existingDriver.setEmail(updatedDriver.getEmail());

                driverRepo.save(existingDriver);
                return existingDriver;
            } else if (existingUser instanceof BasicUser) {
                // It's a BasicUser - fetch from basicUser repo and update
                BasicUser existingBasicUser = basicUserRepo.findById(userId).orElse(null);
                if (existingBasicUser == null) {
                    throw new RuntimeException("BasicUser with ID " + userId + " not found");
                }

                // Deserialize the incoming data
                BasicUser updatedBasicUser = objectMapper.readValue(userJson, BasicUser.class);

                // Update fields
                existingBasicUser.setLogin(updatedBasicUser.getLogin());
                if (updatedBasicUser.getPassword() != null && !updatedBasicUser.getPassword().isBlank()) {
                    existingBasicUser.setPassword(encoder.encode(updatedBasicUser.getPassword()));
                }
                existingBasicUser.setName(updatedBasicUser.getName());
                existingBasicUser.setSurname(updatedBasicUser.getSurname());
                existingBasicUser.setPhone_number(updatedBasicUser.getPhone_number());
                existingBasicUser.setAddress(updatedBasicUser.getAddress());
                existingBasicUser.setEmail(updatedBasicUser.getEmail());
                existingBasicUser.setDateModified(java.time.LocalDateTime.now());

                basicUserRepo.save(existingBasicUser);
                return existingBasicUser;
            }
//            else {
//                // It's a regular User
//                User updatedUser = objectMapper.readValue(userJson, User.class);
//
//                // Update fields
//                existingUser.setLogin(updatedUser.getLogin());
//                existingUser.setPassword(updatedUser.getPassword());
//                existingUser.setName(updatedUser.getName());
//                existingUser.setSurname(updatedUser.getSurname());
//                existingUser.setPhone_number(updatedUser.getPhone_number());
//
//                userRepo.save(existingUser);
                else {return existingUser;}
//            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }
    @GetMapping("getUserById/{id}")
    public User getUserById(@PathVariable int id) {
        User u = driverRepo.findById(id).orElse(null);
        if (u != null) return u;
        return basicUserRepo.findById(id).orElse(null);
    }

}
