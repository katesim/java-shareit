package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(long id) throws NotFoundException;

    User add(User user);

    User update(long id, User user) throws NotFoundException;

    void delete(long id) throws NotFoundException;
}
