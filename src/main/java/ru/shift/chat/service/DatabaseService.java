package ru.shift.chat.service;

import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User add(User user);

    List<User> getAll();

    User get(int userId);

    User update(int userId, User user);

}
