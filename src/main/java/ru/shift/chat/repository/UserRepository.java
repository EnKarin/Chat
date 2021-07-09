package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
}
