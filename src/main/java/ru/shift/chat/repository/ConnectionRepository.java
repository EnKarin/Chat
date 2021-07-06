package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Connection;

public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
}
