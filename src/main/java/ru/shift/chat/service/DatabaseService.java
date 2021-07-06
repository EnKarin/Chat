package ru.shift.chat.service;

import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Connection;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User addUser(User user);

    List<User> getAll();

    User getUser(int userId);

    User updateUser(int userId, User user);

    Message addMessage(Message message, String time);

    List<Message> getAllMessage();

    Chat addChat(Chat chat);

    List<Chat> getAllChat();

    void enterChat(Connection connection);

    void leaveChat(Connection connection);

    void addMessage(Message message, String time, int chatId);

    List<Message> getAllMessageInCurrentChat(int idChat);
}
