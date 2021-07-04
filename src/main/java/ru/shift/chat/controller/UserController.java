package ru.shift.chat.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.enums.ErrorCode;
import ru.shift.chat.model.ErrorDTO;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    Gson gson;

    @PostMapping("/user")
    private ResponseEntity<String> saveUser(@Valid User user, BindingResult result){
        try {
            if(result.hasErrors()){
                return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT)),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(gson.toJson(databaseService.add(user)),HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR)),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    private ResponseEntity<List<String>> getAllUser(){
        try {
            return new ResponseEntity<>(databaseService.getAll().stream()
                    .map(u -> gson.toJson(u))
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(List.of(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR))),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    private ResponseEntity<String> getUser(@PathVariable int userId){
        try{
            User user = databaseService.get(userId);
            if(user == null) {
                return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCORRECT_ID)),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(gson.toJson(user), HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR)),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/{userId}")
    private ResponseEntity<String> updateUser(@PathVariable int userId,
                                          @Valid User user, BindingResult result){
        if(result.hasErrors()){
            return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT)),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            User updateUser = databaseService.update(userId, user);
            if(updateUser == null) {
                return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCORRECT_ID)),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(gson.toJson(user), HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR)),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
