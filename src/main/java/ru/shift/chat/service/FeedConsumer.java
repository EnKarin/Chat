package ru.shift.chat.service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Chat;

@Service
public class FeedConsumer {

    @Autowired
    DatabaseService databaseService;

    public void saveFirstRssMessage(Chat chat) throws FeedException, ConnectionNotFoundException {
        SyndEntry syndEntry = (rssReader(chat.getRssLink().get())).get(0);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatId(chat.getChatId());
        messageDTO.setUserId(-1);
        messageDTO.setLifetimeSec(-1);
        messageDTO.setText(syndEntry.getDescription().getValue().trim());
        messageDTO.setSendTime(LocalDateTime.now().toString());
        databaseService.addMessage(messageDTO);

        chat.setLastMessageDateTime(syndEntry.getPublishedDate());

        databaseService.saveExistChat(chat);
    }

    public void saveRssMessage(){
        List<Chat> chats = databaseService.getAllChat().stream()
                .filter(chat -> chat.getRssLink().isPresent())
                .collect(Collectors.toList());

        List<MessageDTO> messageDTOS = Stream.generate(MessageDTO::new)
                .limit(chats.size())
                .peek(messageDTO -> messageDTO.setUserId(-1))
                .peek(messageDTO -> messageDTO.setLifetimeSec(-1))
                .collect(Collectors.toList());

        Iterator<Chat> chatIterator = chats.iterator();
        messageDTOS.forEach(messageDTO -> {
            Chat chat = chatIterator.next();
            messageDTO.setSendTime(LocalDateTime.now().toString());
            messageDTO.setChatId(chat.getChatId());
            try {
                List<SyndEntry> rssNews = rssReader(chat.getRssLink().get());
                Date date = rssNews.get(0).getPublishedDate();
                for(SyndEntry entry : rssNews){
                    String mess = entry.getDescription().getValue().trim();
                    messageDTO.setText(mess);
                    if(!entry.getPublishedDate().after(chat.getLastMessageDateTime())) {
                        chat.setLastMessageDateTime(date);
                        break;
                    }
                    databaseService.addMessage(messageDTO);
                }
            } catch (FeedException | ConnectionNotFoundException e) {
                e.printStackTrace();
            }
        });
        databaseService.saveAllChat(chats);
    }

    private static List<SyndEntry> rssReader(String rssURL) throws FeedException {
        try (XmlReader reader = new XmlReader(new URL(rssURL))) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            return feed.getEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
