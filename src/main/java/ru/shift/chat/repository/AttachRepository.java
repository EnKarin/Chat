package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Attach;

public interface AttachRepository extends CrudRepository<Attach, String> {
}
