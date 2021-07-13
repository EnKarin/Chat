package ru.shift.chat.service;

import com.rometools.rome.io.FeedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import ru.shift.chat.DTO.AttachDTO;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.*;
import ru.shift.chat.repository.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
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

    @Autowired
    AttachRepository attachRepository;

    @Autowired
    FeedConsumer consumer;

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
    public Chat addChat(Chat chat) throws ConnectionNotFoundException, FeedException {
        try {
            chat = chatRepository.save(chat);
            if (chat.getRssLink().isPresent()) {
                consumer.saveFirstRssMessage(chat);
            }
            return chat;
        } catch (NoSuchElementException | FeedException e) {
            chatRepository.delete(chat);
            throw e;
        }
    }

    @Override
    public List<Chat> getAllChat() {
        return (List<Chat>) chatRepository.findAll();
    }

    public void saveAllChat(List<Chat> chats) {
        chatRepository.saveAll(chats);
    }

    public void saveExistChat(Chat chat) {
        chatRepository.save(chat);
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

            while (uncheckedIterator.hasNext())
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

        if (messageDTO.getUserId() == -1 || hasConnection(message.getChat(), message.getUserId())) {
            List<Unchecked> unchecks = List.of();
            if (message.getChat().getConnections() != null) { // false только в случае, если в чате нет пользователей
                unchecks = createUncheckedForMessage(message);
            }

            Message result = messageRepository.save(message);
            if (!unchecks.isEmpty()) {
                unchecks.forEach(unchecked -> unchecked.setMessage(result));
                uncheckedRepository.saveAll(unchecks);
            }
            return result;
        }
        throw new ConnectionNotFoundException();
    }

    @Override
    public String addMessage(AttachDTO attachDTO) throws ConnectionNotFoundException, IOException {
        Message message = new Message();
        message.setUserId(attachDTO.getUserId());
        message.setSendTime(LocalDateTime.now().toString());
        message.setLifetimeSec(-1);
        message.setChat(chatRepository.findById(attachDTO.getChatId()).get());

        if (hasConnection(message.getChat(), message.getUserId())) {
            Attach attach = new Attach();
            attach.setData(attachDTO.getFile());
            String name = attachDTO.getFile().getOriginalFilename();
            attach.setExpansion(name.substring(name.indexOf('.')));
            attach = attachRepository.save(attach);

            message.setAttach(attach.getName() + attach.getExpansion());
            List<Unchecked> unchecks = createUncheckedForMessage(message);
            Message result = messageRepository.save(message);
            unchecks.forEach(unchecked -> unchecked.setMessage(result));
            uncheckedRepository.saveAll(unchecks);
            return attach.getName() + attach.getExpansion();
        }
        throw new ConnectionNotFoundException();
    }

    @Override
    public List<Message> getAllMessage(int chatId, int userId) throws ConnectionNotFoundException {
        Chat chat = chatRepository.findById(chatId).get();
        if (hasConnection(chat, userId)) {
            userCheckMessage(chatId, userId);

            return chat.getMessages().stream()
                    .filter(message -> message.getLifetimeSec() == -1
                            || (LocalDateTime
                            .parse(message.getSendTime())
                            .plusSeconds(message.getLifetimeSec())
                            .isAfter(LocalDateTime.now())))
                    .filter(message -> LocalDateTime.parse(message.getSendTime())
                            .isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Message::getSendTime).reversed())
                    .collect(Collectors.toList());
        }
        throw new ConnectionNotFoundException();
    }

    @Override
    public List<Message> getAllUnreadMessages(int chatId, int userId) throws ConnectionNotFoundException {
        if (hasConnection(chatRepository.findById(chatId).get(), userId)) {
            List<Unchecked> unchecks = userCheckMessage(chatId, userId);

            return unchecks.parallelStream()
                    .filter(unchecked -> unchecked.getMessage().getLifetimeSec() == -1
                            || (LocalDateTime
                            .parse(unchecked.getMessage().getSendTime())
                            .plusSeconds(unchecked.getMessage().getLifetimeSec())
                            .isAfter(LocalDateTime.now())))
                    .map(Unchecked::getMessage)
                    .sorted(Comparator.comparing(Message::getSendTime).reversed())
                    .collect(Collectors.toList());
        }
        throw new ConnectionNotFoundException();
    }

    @Override
    public Attach getAttach(String name) {
        return attachRepository.findById(name).get();
    }

    private boolean hasConnection(Chat chat, int userId) {
        return chat.getChatId() == 0 || chat.getConnections().stream()
                .map(Connection::getUser)
                .mapToInt(User::getUserId)
                .anyMatch(id -> userId == id);
    }

    private List<Unchecked> userCheckMessage(int chatId, int userId) {
        List<Unchecked> unchecked = userRepository.findById(userId).get().getUnchecked().parallelStream()
                .filter(uncheck -> uncheck.getChatId() == chatId)
                .filter(uncheck -> LocalDateTime.parse(uncheck.getMessage().getSendTime())
                        .isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        uncheckedRepository.deleteAll(unchecked);
        return unchecked;
    }

    private List<Unchecked> createUncheckedForMessage(Message message) {
        List<User> users = message.getChat().getConnections()
                .stream()
                .map(Connection::getUser)
                .filter(user -> user.getUserId() != message.getUserId())
                .collect(Collectors.toList());

        List<Unchecked> result = Stream.generate(Unchecked::new)
                .limit(users.size())
                .peek(unchecked -> unchecked.setChatId(message.getChat().getChatId()))
                .collect(Collectors.toList());

        Iterator<Unchecked> uncheckedIterator = result.iterator();
        Iterator<User> usersIterator = users.iterator();

        while (uncheckedIterator.hasNext())
            uncheckedIterator.next().setUser(usersIterator.next());

        return result;
    }
}
