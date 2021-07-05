package ru.shift.chat.service;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);
}
