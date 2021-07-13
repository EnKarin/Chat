package ru.shift.chat.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.DTO.ErrorDTO;
import ru.shift.chat.enums.ErrorCode;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.service.AttachService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@Api(tags = {TagsConstant.ATTACH_TAG})
public class AttachController {

    @Autowired
    AttachService attachService;

    @Autowired
    Gson gson;

    @ExceptionHandler({Exception.class})
    private ResponseEntity<?> unknownError() {
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.UNKNOWN_ERROR)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NoSuchElementException.class})
    private ResponseEntity<?> noSuchElementError() {
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCORRECT_ID)),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConnectionNotFoundException.class, IOException.class})
    private ResponseEntity<?> connectionError(){
        return new ResponseEntity<>(gson.toJson(new ErrorDTO(ErrorCode.INCOMPLETE_INPUT)),
                HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Sends a file to the general chat")
    @PostMapping(value = "/attach")
    private String saveFile(@ModelAttribute AttachDTO attachDTO) throws ConnectionNotFoundException, IOException {
        return attachService.saveAttach(attachDTO);
    }

    @ApiOperation(value = "Downloads a file from the server")
    @GetMapping(value = "/attach/{file:.+}")
    private void downloadFile(HttpServletResponse response,
                                @PathVariable("file")String file) throws IOException {
        attachService.downloadAttach(response, file);
    }
}
