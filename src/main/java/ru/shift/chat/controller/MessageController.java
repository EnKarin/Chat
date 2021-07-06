package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class MessageController {

    @Autowired
    DatabaseService databaseService;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler({ChatNotFoundException.class, NoSuchElementException.class})
    private void notFound(){}

    @PostMapping("/message")
    private Message saveMessage(@RequestBody Map<String, String> map) throws ChatNotFoundException{
        map.putIfAbsent("chatId", "0");
        Message message = new Message();
        message.setUserId(Integer.parseInt(map.get("userId")));
        message.setText(map.get("text"));
        message.setSendTime(LocalDateTime.now().toString());
        return databaseService.addMessage(message, Integer.parseInt(map.get("chatId")));
    }

    @GetMapping("/messages")
    private List<Message> getMessages(@RequestParam(required = false) Integer chatId){
        if(chatId == null)
            chatId = 0;
        return databaseService.getAllMessageInCurrentChat(chatId);
    }
}
