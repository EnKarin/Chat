package ru.shift.chat.controller;

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
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Api(tags = {TagsConstant.MESSAGE_TAG})
@RestController
public class MessageController {

    @Autowired
    DatabaseService databaseService;

    @ExceptionHandler({ConnectionNotFoundException.class})
    private ResponseEntity<?> connectNotFound() {
        return new ResponseEntity<>(ToGson.ErrorToGson(ErrorCode.ACCESS_ERROR),
                HttpStatus.FORBIDDEN);
    }

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

    @ApiOperation(value = "Sending a message to the specified chat. If the chat is not specified, then the general",
            response = Message.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "No chat access"),
            @ApiResponse(code = 404, message = "Chat or user not found"),
            @ApiResponse(code = 500, message = "Unknown server error")
    })
    @PostMapping("/message")
    private Message saveMessage(@RequestBody MessageDTO messageDTO) throws ConnectionNotFoundException {
        messageDTO.setSendTime(LocalDateTime.now().plusSeconds(messageDTO.getDelaySec()).toString());
        if(messageDTO.getLifetimeSec() == null) messageDTO.setLifetimeSec(-1);
        Message message = databaseService.addMessage(messageDTO);
        message.toUserView();
        return message;
    }

    @ApiOperation(value = "Retrieve all messages from a private chat. If the chat is not specified, then from the general",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "No chat access"),
            @ApiResponse(code = 404, message = "Chat or user not found"),
            @ApiResponse(code = 500, message = "Unknown server error")
    })
    @GetMapping("/messages")
    private List<Message> getMessages(@RequestParam Integer userId,
                                      @RequestParam(required = false, defaultValue = "0") Integer chatId) throws ConnectionNotFoundException {
        return databaseService.getAllMessage(chatId, userId).stream().peek(Message::toUserView).collect(Collectors.toList());
    }

    @ApiOperation(value = "Returns unread messages for the given user in the specified chat",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "No chat access"),
            @ApiResponse(code = 404, message = "Chat or user not found"),
            @ApiResponse(code = 500, message = "Unknown server error")
    })
    @GetMapping("/messages/unread")
    private List<Message> getUnreadMessages(@RequestParam Integer userId,
                                           @RequestParam(required = false, defaultValue = "0") Integer chatId) throws ConnectionNotFoundException {
        return databaseService.getAllUnreadMessages(chatId, userId).stream().peek(Message::toUserView).collect(Collectors.toList());
    }
}
