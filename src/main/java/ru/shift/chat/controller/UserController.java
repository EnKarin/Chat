package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseService;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @PostMapping("/user")
    private User saveUser(User user){
        return databaseService.add(user);
    }

    @GetMapping("/users")
    private List<User> getAllUser(){
        return databaseService.getAll();
    }

    @GetMapping("/user/{userId}")
    private User getUser(@PathVariable int userId){
        return databaseService.get(userId);
    }

    @PutMapping("/user/{userId}")
    private User updateUser(@PathVariable int userId,
                                          User user){
        return databaseService.update(userId, user);
    }
}
