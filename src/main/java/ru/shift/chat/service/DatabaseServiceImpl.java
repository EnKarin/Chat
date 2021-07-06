package ru.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.chat.exception.ChatNotFoundException;
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
import java.util.Optional;

@Service
public class DatabaseServiceImpl implements DatabaseService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ConnectionRepository connectionRepository;

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
    public Chat addChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public List<Chat> getAllChat() {
        return (List<Chat>) chatRepository.findAll();
    }

    @Override
    public void enterChat(int userId, int chatId) {
        Connection connection = new Connection();
        connection.setChat(chatRepository.findById(chatId).get());
        connection.setUser(userRepository.findById(userId).get());
        connectionRepository.save(connection);
    }

    @Override
    public void leaveChat(int userId, int chatId) {
        Connection connection = new Connection();
        connection.setChat(chatRepository.findById(chatId).get());
        connection.setUser(userRepository.findById(userId).get());
        connectionRepository.delete(connection);
    }

    @Override
    public Message addMessage(Message message, String time, int chatId) throws ChatNotFoundException {
        message.setSendTime(time);
        message.setChat(chatRepository.findById(chatId).get());
        if(chatId == 0 || message.getChat().getConnections().stream()
                .map(Connection::getUser)
                .mapToInt(User::getUserId)
                .anyMatch(id -> message.getUserId() == id)){
            return messageRepository.save(message);
        }
        throw new ChatNotFoundException();
    }

    @Override
    public List<Message> getAllMessageInCurrentChat(int idChat) {
        List<Message> list = chatRepository.findById(idChat).orElse(new Chat()).getMessages();
        list.sort(Comparator.comparing(Message::getSendTime).reversed());
        return list;
    }

    public List<User> findByFirstName(final String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public List<User> findByLastName(final String lastName) {
        return userRepository.findByLastName(lastName);
    }
}
