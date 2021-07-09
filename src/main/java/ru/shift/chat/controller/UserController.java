package ru.shift.chat.controller;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Validated
@Api(tags = {TagsConstant.USER_TAG})
public class UserController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ValidatorImpl validator;

    @Autowired
    Gson gson;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    private ResponseEntity<?> notFound() {
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT)),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    private ResponseEntity<?> unknownError() {
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NoSuchElementException.class})
    private ResponseEntity<?> noSuchElementError() {
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCORRECT_ID)), HttpStatus.NOT_FOUND);
    }


    @ApiOperation(value = "Saving a user", tags = "saveOrUpdateUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect data"),
            @ApiResponse(code = 500, message = "Connection error")})
    @PostMapping("/user")
    private User saveUser(@RequestBody @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException();
        }
        return databaseService.addUser(user);
    }

    @ApiOperation(value = "Getting a list of all users", tags = "getUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 500, message = "Connection error")})
    @GetMapping("/users")
    private List<User> getAllUser() {
        return databaseService.getAllUsers();
    }

    @ApiOperation(value = "Getting a user by his id", tags = "getUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect id"),
            @ApiResponse(code = 500, message = "Connection error")})
    @GetMapping("/user/{userId}")
    private User getUser(@PathVariable int userId) {
        return databaseService.getUser(userId);
    }

    @ApiOperation(value = "Editing user by id", tags = "saveOrUpdateUser")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Incorrect id or data"),
            @ApiResponse(code = 500, message = "Connection error")})
    @PutMapping("/user/{userId}")
    private User updateUser(@PathVariable int userId,
                            @RequestBody @Valid User user,
                            BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException();
        }
        return databaseService.updateUser(userId, user);
    }
}
