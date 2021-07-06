package ru.shift.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.shift.chat.model.User;

@Service
public class ValidatorImpl implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(User.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.empty");
    }
}
