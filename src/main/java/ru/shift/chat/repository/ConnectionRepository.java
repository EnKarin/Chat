package ru.shift.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Connection;

import java.util.List;

public interface ConnectionRepository extends CrudRepository<Connection, Integer>, JpaRepository<Connection, Integer> {
    @Query(value = "select id from connection where user_id = ?1 and chat_id = ?2", nativeQuery = true)
    List<Integer> findByUserIdAndChatId(int userId, int chatId);
}
