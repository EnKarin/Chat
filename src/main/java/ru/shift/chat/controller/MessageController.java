package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class MessageController {

    @Autowired
    DatabaseService databaseService;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler({ChatNotFoundException.class, NoSuchElementException.class})
    private void notFound(){}

    @PostMapping("/message")
    private Message saveMessage(@RequestBody Message message,
                                @RequestParam(required = false, defaultValue = "0") int chatId){
        if(chatId == 0)
            return databaseService.addMessage(message, LocalDateTime.now().toString());
        else return databaseService.addMessage(message, LocalDateTime.now().toString(), chatId);
    }

    @GetMapping("/messages")
    private List<Message> getMessages(@RequestParam(required = false, defaultValue = "0") int chatId){
        return databaseService.getAllMessageInCurrentChat(chatId);
    }
}
