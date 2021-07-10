package ru.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.*;
import ru.shift.chat.repository.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    UncheckedRepository uncheckedRepository;

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

            List<Message> messages = connection.getChat().getMessages();
            List<Unchecked> uncheckeds = Stream.generate(Unchecked::new)
                    .limit(messages.size())
                    .peek(unchecked -> unchecked.setUser(connection.getUser()))
                    .peek(unchecked -> unchecked.setChatId(chatId))
                    .collect(Collectors.toList());

            Iterator<Unchecked> uncheckedIterator = uncheckeds.iterator();
            Iterator<Message> messagesIterator = messages.iterator();

            while(uncheckedIterator.hasNext())
                uncheckedIterator.next().setMessage(messagesIterator.next());

            uncheckedRepository.saveAll(uncheckeds);
        }
    }

    @Override
    public void leaveChat(int userId, int chatId) {
        connectionRepository.deleteAllById(connectionRepository.findByUserIdAndChatId(userId, chatId));
    }

    @Override
    public Message addMessage(MessageDTO messageDTO) throws ConnectionNotFoundException {
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setUserId(messageDTO.getUserId());
        message.setSendTime(messageDTO.getSendTime());
        message.setLifetimeSec(messageDTO.getLifetimeSec());
        message.setChat(chatRepository.findById(messageDTO.getChatId()).get());

        if (messageDTO.getChatId() == 0
                || message.getChat().getConnections().stream()
                .map(Connection::getUser)
                .mapToInt(User::getUserId)
                .anyMatch(id -> message.getUserId() == id)) {
            List<User> usersId = chatRepository.findById(messageDTO.getChatId()).get().getConnections()
                    .stream()
                    .map(Connection::getUser)
                    .filter(user -> user.getUserId() != message.getUserId())
                    .collect(Collectors.toList());

            List<Unchecked> uncheckeds = Stream.generate(Unchecked::new)
                    .limit(usersId.size())
                    .peek(unchecked -> unchecked.setMessage(message))
                    .peek(unchecked -> unchecked.setChatId(messageDTO.getChatId()))
                    .collect(Collectors.toList());

            Iterator<Unchecked> uncheckedIterator = uncheckeds.iterator();
            Iterator<User> usersIterator = usersId.iterator();

            while(uncheckedIterator.hasNext())
                uncheckedIterator.next().setUser(usersIterator.next());

            uncheckedRepository.saveAll(uncheckeds);

            Message result = messageRepository.save(message);
            result.toUserView();
            return result;
        }
        throw new ConnectionNotFoundException();
    }

    @Override
    public List<Message> getAllMessage(int chatId, int userId) {
        List<Unchecked> unchecked = userRepository.findById(userId).get().getUnchecked().parallelStream()
                .filter(uncheck -> uncheck.getChatId() == chatId)
                .collect(Collectors.toList());

        uncheckedRepository.deleteAll(unchecked);

        return chatRepository.findById(chatId).get().getMessages().stream()
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

    @Override
    public List<Message> getAllUnreadMessages(int chatId, int userId){
        List<Unchecked> uncheckeds = userRepository.findById(userId).get().getUnchecked()
                .parallelStream()
                .filter(uncheck -> uncheck.getChatId() == chatId)
                .filter(unchecked -> unchecked.getMessage().getLifetimeSec() == -1
                        || (LocalDateTime
                        .parse(unchecked.getMessage().getSendTime())
                        .plusSeconds(unchecked.getMessage().getLifetimeSec())
                        .isAfter(LocalDateTime.now())))
                .filter(unchecked -> LocalDateTime.parse(unchecked.getMessage().getSendTime())
                        .isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        uncheckedRepository.deleteAll(uncheckeds);
        return uncheckeds.parallelStream()
                .map(Unchecked::getMessage)
                .sorted(Comparator.comparing(Message::getSendTime).reversed())
                .peek(Message::toUserView)
                .collect(Collectors.toList());
    }
}
