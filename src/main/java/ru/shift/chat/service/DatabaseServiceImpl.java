package ru.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Connection;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;
import ru.shift.chat.repository.ChatRepository;
import ru.shift.chat.repository.ConnectionRepository;
import ru.shift.chat.repository.MessageRepository;
import ru.shift.chat.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    ChatRepository chatRepository;

    @Override
    public User addUser(final User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUser(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User updateUser(int userId, User user) {
        user.setUserId(userId);
        return (userRepository.findById(userId).isPresent()? userRepository.save(user): null);
    }

    @Override
    public Message addMessage(Message message, String time) {
        message.setSendTime(time);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getAllMessage() {
        List<Message> list = (List<Message>) messageRepository.findAll();
        list.sort(Comparator.comparing(Message::getSendTime).reversed());
        return list;
    }

    @Override
    public Chat addChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public List<Chat> getAllChat() {
        return (List<Chat>) chatRepository.findAll();
    }

    @Override
    public void enterChat(Connection connection) {
        connectionRepository.save(connection);
    }

    @Override
    public void leaveChat(Connection connection) {
        connectionRepository.delete(connection);
    }

    @Override
    public void addMessage(Message message, String time, int chatId) {
        message.setChat(chatRepository.findById(chatId).get());
        addMessage(message, time);
    }

    @Override
    public List<Message> getAllMessageInCurrentChat(int idChat) {
        return chatRepository.findById(idChat).orElse(new Chat()).getMessages();
    }

    public List<User> findByFirstName(final String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public List<User> findByLastName(final String lastName) {
        return userRepository.findByLastName(lastName);
    }
}
