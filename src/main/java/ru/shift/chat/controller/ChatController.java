package ru.shift.chat.controller;

import com.rometools.rome.io.FeedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.config.ToGson;
import ru.shift.chat.enums.ErrorCode;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Chat;
import ru.shift.chat.service.DatabaseService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Api(tags = {TagsConstant.CHAT_TAG})
public class ChatController {

    @Autowired
    DatabaseService databaseService;

    @ExceptionHandler({Exception.class})
    private ResponseEntity<?> unknownError() {
        return new ResponseEntity<>(ToGson.ErrorToGson(ErrorCode.UNKNOWN_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NoSuchElementException.class})
    private ResponseEntity<?> noSuchElementError() {
        return new ResponseEntity<>(ToGson.ErrorToGson(ErrorCode.INCORRECT_ID),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FeedException.class})
    private ResponseEntity<?> feedError(){
        return new ResponseEntity<>(ToGson.ErrorToGson(ErrorCode.INCOMPLETE_INPUT),
                HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Create a chat with a given name", response = Chat.class)
    @PostMapping("/chat")
    private Chat createChat(@RequestBody Chat chat) throws ConnectionNotFoundException, FeedException {
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
            @ApiResponse(code = 404, message = "Chat or user not found"),
            @ApiResponse(code = 500, message = "Unknown server error")
    })
    @PostMapping("/chat/enter")
    private void enterTheChat(@RequestBody MessageDTO messageDTO) throws NoSuchElementException{
        databaseService.enterChat(messageDTO.getUserId(), messageDTO.getChatId());
    }

    @ApiOperation("Disconnect a user from this chat")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "The user is not in the chat or the chat does not exist"),
            @ApiResponse(code = 500, message = "Unknown server error")
    })
    @PostMapping("/chat/leave")
    private void quitChat(@RequestBody MessageDTO messageDTO) throws NoSuchElementException{
        databaseService.leaveChat(messageDTO.getUserId(), messageDTO.getChatId());
    }
}
