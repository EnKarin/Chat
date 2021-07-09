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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DatabaseServiceImpl implements DatabaseService {

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
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUser(int userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public User updateUser(int userId, User user) {
        user.setUserId(userId);
        if (userRepository.findById(userId).isPresent()) {
            return userRepository.save(user);
        }
        throw new NoSuchElementException();
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
        if (connectionRepository.findByUserIdAndChatId(userId, chatId).isEmpty()) {
            Connection connection = new Connection();
            connection.setChat(chatRepository.findById(chatId).get());
            connection.setUser(userRepository.findById(userId).get());
            connectionRepository.save(connection);
        }
    }

    @Override
    public void leaveChat(int userId, int chatId) {
        connectionRepository.deleteAllById(connectionRepository.findByUserIdAndChatId(userId, chatId));
    }

    @Override
    public Message addMessage(Message message, int chatId) throws ChatNotFoundException {
        message.setChat(chatRepository.findById(chatId).get());
        if (chatId == 0
                || message.getChat().getConnections().stream()
                .map(Connection::getUser)
                .mapToInt(User::getUserId)
                .anyMatch(id -> message.getUserId() == id)) {
            Message result = messageRepository.save(message);
            result.toUserView();
            return result;
        }
        throw new ChatNotFoundException();
    }

    @Override
    public List<Message> getAllMessageInCurrentChat(int chatId, int userId) {
        return chatRepository.findById(chatId).get().getMessages()
                .stream()
                .filter(message -> message.getLifetimeSec() == -1
                        || (LocalDateTime
                        .parse(message.getSendTime())
                        .plusSeconds(message.getLifetimeSec())
                        .isAfter(LocalDateTime.now())))
                .filter(message -> LocalDateTime.parse(message.getSendTime())
                        .isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Message::getSendTime).reversed())
                .peek(Message::toUserView)
                .collect(Collectors.toList());
    }
}
