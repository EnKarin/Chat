package ru.shift.chat.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);
}
