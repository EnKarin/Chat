package ru.shift.chat.controller;

import com.rometools.rome.io.FeedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.service.DatabaseService;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = {TagsConstant.ATTACH_TAG})
public class AttachController {

    @Autowired
    DatabaseService databaseService;

    @ApiOperation(value = "Sends a file to the general chat")
    @PostMapping(value = "/attach")
    private String saveFile(@ModelAttribute AttachDTO attachDTO) {
        return "ok";
    }

    @ApiOperation(value = "Downloads a file from the server")
    @GetMapping(value = "/attach/{file:.+}")
    private void downloadFile(HttpServletResponse response,
                                @PathVariable("file")String file) {

    }
}
