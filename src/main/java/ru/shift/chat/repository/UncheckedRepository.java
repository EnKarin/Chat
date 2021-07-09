package ru.shift.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.shift.chat.model.Unchecked;

public interface UncheckedRepository extends CrudRepository<Unchecked, Integer> {
}
