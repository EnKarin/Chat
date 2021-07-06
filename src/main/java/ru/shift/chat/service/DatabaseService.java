package ru.shift.chat.service;

import ru.shift.chat.exception.ChatNotFoundException;
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User addUser(User user);

    List<User> getAll();

    User getUser(int userId);

    User updateUser(int userId, User user);

    Chat addChat(Chat chat);

    List<Chat> getAllChat();

    void enterChat(int userId, int chatId);

    void leaveChat(int userId, int chatId);

    Message addMessage(Message message, int chatId) throws ChatNotFoundException;

    List<Message> getAllMessageInCurrentChat(int idChat);
}
