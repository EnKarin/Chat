package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Message;

public interface MessageRepository extends CrudRepository<Message, Integer> {
}
