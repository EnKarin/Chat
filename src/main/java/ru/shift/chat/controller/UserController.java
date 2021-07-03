package ru.shift.chat.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    Gson gson;

    @PostMapping("/user")
    private String saveUser(User user){
        return gson.toJson(databaseService.add(user));
    }

    @GetMapping("/users")
    private List<String> getAllUser(){
        return databaseService.getAll().stream().map(u -> gson.toJson(u)).collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}")
    private String getUser(@PathVariable int userId){
        return gson.toJson(databaseService.get(userId));
    }

    @PutMapping("/user/{userId}")
    private String updateUser(@PathVariable int userId,
                                          User user){
        return gson.toJson(databaseService.update(userId, user));
    }
}
