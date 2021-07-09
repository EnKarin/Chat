package ru.shift.chat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Message;
import ru.shift.chat.service.DatabaseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private Message saveMessage(@RequestBody Map<String, String> map) throws ChatNotFoundException{
        LocalDateTime localDateTime = LocalDateTime.now();
        map.putIfAbsent("chatId", "0");
        Message message = new Message();
        message.setUserId(Integer.parseInt(map.get("userId")));
        message.setText(map.get("text"));
        if(map.containsKey("lifetimeSec"))
            message.setLifetimeSec(Integer.parseInt(map.get("lifetimeSec")));
        else message.setLifetimeSec(-1);
        if(map.containsKey("delaySec"))
            message.setSendTime(localDateTime.plusSeconds(Long.parseLong(map.get("delaySec"))).toString());
        else message.setSendTime(localDateTime.toString());
        return databaseService.addMessage(message, Integer.parseInt(map.get("chatId")));
    }

    @ApiOperation(value = "Retrieve all messages from a private chat. If the chat is not specified, then from the general",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")})
    @GetMapping("/messages")
    private List<Message> getMessages(@RequestParam Integer userId,
                                      @RequestParam(required = false) Integer chatId){
        return databaseService.getAllMessage(chatId == null? 0: chatId, userId);
    }

    @ApiOperation(value = "Returns unread messages for the given user in the specified chat",
            response = List.class, tags = "getMessages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "Chat or user not found")})
    @GetMapping("/messages/unread")
    private List<Message> getUnreadMessages(@PathVariable int userId,
                                           @PathVariable(required = false, value = "0") int chatId){
        return databaseService.getAllUnreadMessages(userId, chatId);
    }
}
