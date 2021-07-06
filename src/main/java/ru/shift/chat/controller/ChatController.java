package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.model.Chat;
import ru.shift.chat.service.DatabaseService;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    DatabaseService databaseService;

    @PostMapping("/chat")
    private Chat createChat(@RequestParam String name){
        Chat chat = new Chat();
        chat.setName(name);
        return databaseService.addChat(chat);
    }

    @GetMapping("/chats")
    private List<Chat> getAllChat(){
        return databaseService.getAllChat();
    }

    @PostMapping("/chat/enter")
    private void enterTheChat(@RequestParam int userId,
                             @RequestParam int chatId){
        databaseService.enterChat(userId, chatId);
    }

    @PostMapping("/chat/leave")
    private void quitChat(@RequestParam int userId,
                          @RequestParam int chatId){
        databaseService.leaveChat(userId, chatId);
    }
}
