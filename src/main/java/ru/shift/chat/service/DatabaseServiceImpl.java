package ru.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.chat.model.Message;
import ru.shift.chat.repository.MessageRepository;
import ru.shift.chat.repository.UserRepository;
import ru.shift.chat.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    public User getById(final int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User get(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User update(int userId, User user) {
        user.setUserId(userId);
        return (userRepository.findById(userId).isPresent()? userRepository.save(user): null);
    }

    @Override
    public Message add(Message message, String time) {
        message.setSendTime(time);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getAllMessage() {
        List<Message> list = (List<Message>) messageRepository.findAll();
        list.sort(Comparator.comparing(Message::getSendTime).reversed());
        return list;
    }

    public User add(final User user) {
        return userRepository.save(user);
    }

    public List<User> findByFirstName(final String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public List<User> findByLastName(final String lastName) {
        return userRepository.findByLastName(lastName);
    }
}
