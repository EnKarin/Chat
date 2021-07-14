package ru.shift.chat.service;

import com.rometools.rome.io.FeedException;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Attach;
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DatabaseService {

    User addUser(User user);

    List<User> getAllUsers();

    User getUser(int userId);

    User updateUser(int userId, User user);

    Chat addChat(Chat chat) throws ConnectionNotFoundException, FeedException;

    List<Chat> getAllChat();

    String addMessageAttachURL(AttachDTO attachDTO, byte[] file) throws ConnectionNotFoundException;

    void saveAllChat(List<Chat> chats);

    void saveExistChat(Chat chat);

    void enterChat(int userId, int chatId);

    void leaveChat(int userId, int chatId);

    Message addMessage(MessageDTO messageDTO) throws ConnectionNotFoundException;

    String addMessage(AttachDTO attachDTO) throws ConnectionNotFoundException, IOException;

    List<Message> getAllMessage(int chatId, int userId) throws ConnectionNotFoundException;

    List<Message> getAllUnreadMessages(int chatId, int userId) throws ConnectionNotFoundException;

    Attach getAttach(String name);
}
