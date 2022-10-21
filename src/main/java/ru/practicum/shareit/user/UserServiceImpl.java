package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User getById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + id + " несуществует"));
    }

    @Override
    @Transactional
    public User add(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User update(long id, User user) {
        user.setId(id);
        User prevUser = getById(id);
        if (user.getName() != null) {
            prevUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            prevUser.setEmail(user.getEmail());
        }
        return repository.save(prevUser);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = getById(id);
        repository.delete(user);
        log.info("Пользователь с id={} удален", id);
    }
}
