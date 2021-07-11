package ru.shift.chat.service;

import com.rometools.rome.io.FeedException;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User addUser(User user);

    List<User> getAllUsers();

    User getUser(int userId);

    User updateUser(int userId, User user);

    Chat addChat(Chat chat) throws ConnectionNotFoundException, FeedException;

    List<Chat> getAllChat();

    void saveAllChat(List<Chat> chats);

    void saveExistChat(Chat chat);

    void enterChat(int userId, int chatId);

    void leaveChat(int userId, int chatId);

    Message addMessage(MessageDTO messageDTO) throws ConnectionNotFoundException;

    List<Message> getAllMessage(int chatId, int userId);

    List<Message> getAllUnreadMessages(int chatId, int userId);
}
