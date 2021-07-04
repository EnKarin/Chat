package ru.shift.chat.service;

import ru.shift.chat.model.User;

import java.util.List;

public interface DatabaseService {

    User add(User user);

    List<User> getAll();

    User get(int userId); // Если пользователя с таким id не существует, верни null

    User update(int userId, User user); // Тут также, как get

}
