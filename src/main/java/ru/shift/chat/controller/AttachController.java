package ru.shift.chat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.enums.TagsConstant;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.service.AttachService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = {TagsConstant.ATTACH_TAG})
public class AttachController {

    @Autowired
    AttachService attachService;

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
