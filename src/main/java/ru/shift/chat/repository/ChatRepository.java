package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, Integer> {
}
