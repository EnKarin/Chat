package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MessageController {

    @Autowired
    DatabaseService databaseService;

    @PostMapping("/message")
    private Message saveMessage(@RequestBody Message message){
        return databaseService.addMessage(message, LocalDateTime.now().toString());
    }

    @GetMapping("/messages")
    private List<Message> getMessages(){
        return databaseService.getAllMessage();
    }
}
