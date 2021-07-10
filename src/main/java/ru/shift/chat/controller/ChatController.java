package ru.shift.chat.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.model.Chat;
import ru.shift.chat.service.DatabaseService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Api(tags = {TagsConstant.CHAT_TAG})
public class ChatController {

    @Autowired
    DatabaseService databaseService;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    private void noSuchElement(){}

    @ApiOperation(value = "Create a chat with a given name", response = Chat.class)
    @PostMapping("/chat")
    private Chat createChat(@RequestBody Chat chat){
        return databaseService.addChat(chat);
    }

    @ApiOperation(value = "Retrieving all available chats", response = List.class)
    @GetMapping("/chats")
    private List<Chat> getAllChat(){
        return databaseService.getAllChat();
    }

    @ApiOperation("Gaining access for a given user to a specified chat")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")
    })
    @PostMapping("/chat/enter")
    private void enterTheChat(@RequestBody MessageDTO messageDTO) throws NoSuchElementException{
        databaseService.enterChat(messageDTO.getUserId(), messageDTO.getChatId());
    }

    @ApiOperation("Disconnect a user from this chat")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "The user is not in the chat or the chat does not exist")
    })
    @PostMapping("/chat/leave")
    private void quitChat(@RequestBody MessageDTO messageDTO) throws NoSuchElementException{
        databaseService.leaveChat(messageDTO.getUserId(), messageDTO.getChatId());
    }
}
