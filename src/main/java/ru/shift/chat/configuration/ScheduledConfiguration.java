package ru.shift.chat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.shift.chat.model.Message;
import ru.shift.chat.repository.MessageRepository;
import ru.shift.chat.service.FeedConsumer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class ScheduledConfiguration {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    FeedConsumer feedConsumer;

    @Scheduled(fixedDelay = 10000)
    private void scheduledDatabaseUpdate(){
        List<Integer> list = ((List<Message>)messageRepository.findAll())
                .stream()
                .filter(message -> LocalDateTime
                        .parse(message.getSendTime())
                        .plusSeconds(message.getLifetimeSec())
                        .isBefore(LocalDateTime.now())
                        && message.getLifetimeSec() != -1)
                .map(Message::getMessageId)
                .collect(Collectors.toList());
        messageRepository.deleteAllById(list);
    }

    @Scheduled(fixedDelay = 600000)
    private void postRssNews(){
        feedConsumer.saveRssMessage();
    }
}
