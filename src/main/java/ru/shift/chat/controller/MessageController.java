package ru.shift.chat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Api(tags = {TagsConstant.MESSAGE_TAG})
@RestController
public class MessageController {

    @Autowired
    DatabaseService databaseService;

    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler({ChatNotFoundException.class, NoSuchElementException.class})
    private void notFound(){}

    @ApiOperation(value = "Sending a message to the specified chat. If the chat is not specified, then the general",
            response = Message.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")})
    @PostMapping("/message")
    private Message saveMessage(@RequestBody MessageDTO messageDTO) throws ChatNotFoundException{
        LocalDateTime localDateTime = LocalDateTime.now();
        messageDTO.setSendTime(LocalDateTime.now().plusSeconds(messageDTO.getDelaySec()).toString());
        if(messageDTO.getLifetimeSec() == null) messageDTO.setLifetimeSec(-1);
        return databaseService.addMessage(messageDTO);
    }

    @ApiOperation(value = "Retrieve all messages from a private chat. If the chat is not specified, then from the general",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")})
    @GetMapping("/messages")
    private List<Message> getMessages(@RequestParam Integer userId,
                                      @RequestParam(required = false, value = "0") Integer chatId){
        return databaseService.getAllMessage(chatId, userId);
    }

    @ApiOperation(value = "Returns unread messages for the given user in the specified chat",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")})
    @GetMapping("/messages/unread")
    private List<Message> getUnreadMessages(@RequestParam Integer userId,
                                           @RequestParam(required = false, value = "0") Integer chatId){
        return databaseService.getAllUnreadMessages(userId, chatId);
    }
}
