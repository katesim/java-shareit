package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();
    User getById(Long id) throws NotFoundException;
    User add(User user);
    User update(Long id, User user) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
}
