package ru.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Attach;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

@Service
public class AttachService {
    @Autowired
    DatabaseService databaseService;

    public void downloadAttach(HttpServletResponse response, String name) throws IOException {
        Attach result = databaseService.getAttach(name.substring(0, name.indexOf('.')));
        String mimeType = URLConnection.guessContentTypeFromName(result.getName());
        if (mimeType == null) {
            //unknown mimetype so set the mimetype to application/octet-stream
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
        response.setContentLength(result.getSize());

        FileCopyUtils.copy(new ByteArrayInputStream(result.getData()), response.getOutputStream());
    }

    public String saveAttach(AttachDTO attachDTO) throws ConnectionNotFoundException, IOException {
        return String.format("{ \"fileName\": \"%s\" }", databaseService.addMessage(attachDTO));
    }
}
