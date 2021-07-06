package ru.shift.chat.service;

import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User add(User user);

    List<User> getAll();

    User get(int userId);

    User update(int userId, User user);

    Message add(Message message, String time);

    List<Message> getAllMessage();
}
