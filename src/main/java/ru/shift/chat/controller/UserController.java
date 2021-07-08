package ru.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.enums.ErrorCode;
import ru.shift.chat.model.ErrorDTO;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseService;
import ru.shift.chat.service.ValidatorImpl;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@Validated
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ValidatorImpl validator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @PostMapping("/user")
    private ResponseEntity<?> saveUser(@RequestBody @Valid User user, BindingResult result){
        try {
            if(result.hasErrors()){
                return new ResponseEntity<>(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(databaseService.addUser(user),HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.UNKNOWN_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    private ResponseEntity<?> getAllUser(){
        try {
            return new ResponseEntity<>(databaseService.getAllUsers(), HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.UNKNOWN_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    private ResponseEntity<?> getUser(@PathVariable int userId){
        try{
            return new ResponseEntity<>(databaseService.getUser(userId), HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.INCORRECT_ID),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception ignored){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.UNKNOWN_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/{userId}")
    private ResponseEntity<?> updateUser(@PathVariable int userId,
                                              @RequestBody @Valid User user,
                                              BindingResult result){
        if(result.hasErrors()){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(databaseService.updateUser(userId, user), HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.INCORRECT_ID),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception ignored){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.UNKNOWN_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
