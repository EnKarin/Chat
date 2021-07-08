package ru.shift.chat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.enums.ErrorCode;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.model.ErrorDTO;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseService;
import ru.shift.chat.service.ValidatorImpl;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@Validated
@Api(tags = {TagsConstant.USER_TAG})
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ValidatorImpl validator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @ApiOperation(value = "Saving a user", tags = "saveOrUpdateUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect data"),
            @ApiResponse(code = 500, message = "Connection error")})
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

    @ApiOperation(value = "Getting a list of all users", tags = "getUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 500, message = "Connection error")})
    @GetMapping("/users")
    private ResponseEntity<?> getAllUser(){
        try {
            return new ResponseEntity<>(databaseService.getAllUsers(), HttpStatus.OK);
        } catch (Exception ignored){
            return new ResponseEntity<>(new ErrorDTO(ErrorCode.UNKNOWN_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Getting a user by his id", tags = "getUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect id"),
            @ApiResponse(code = 500, message = "Connection error")})
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

    @ApiOperation(value = "Editing user by id", tags = "saveOrUpdateUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect id or data"),
            @ApiResponse(code = 500, message = "Connection error")})
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
