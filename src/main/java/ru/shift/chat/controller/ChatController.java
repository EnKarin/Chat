package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Chat;
import ru.shift.chat.service.DatabaseService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class ChatController {

    @Autowired
    DatabaseService databaseService;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    private void noSuchElement(){}

    @PostMapping("/chat")
    private Chat createChat(@RequestBody Chat chat){
        return databaseService.addChat(chat);
    }

    @GetMapping("/chats")
    private List<Chat> getAllChat(){
        return databaseService.getAllChat();
    }

    @PostMapping("/chat/enter")
    private void enterTheChat(@RequestBody Map<String, Integer> map) throws NoSuchElementException{
        databaseService.enterChat(map.get("userId"), map.get("chatId"));
    }

    @PostMapping("/chat/leave")
    private void quitChat(@RequestBody Map<String, Integer> map) throws NoSuchElementException{
        databaseService.leaveChat(map.get("userId"), map.get("chatId"));
    }
}
