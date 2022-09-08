package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.Exception.ValidationException;

import java.util.Set;

@Slf4j
public class UserValidator {
    public static void validateEmail (String email, Set<String> emails) {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("Email не может быть пустым");
        }

        if (!email.contains("@")) {
            throw new ValidationException("Адрес электронной почты должен содержать символ '@'");
        }

        if (emails.contains(email)) {
            throw new RuntimeException("Email: " + email + " уже используется");
        }
    }

}
