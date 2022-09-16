package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Service
public class UserServiceInMemory implements UserService {
    private long currId = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) throws NotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
    }

    @Override
    public User add(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Email: " + user.getEmail() + " уже используется");
        }
        user.setId(++currId);
        users.put(currId, user);
        emails.add(user.getEmail());
        log.info("Пользователь с id={} создан", user.getId());
        return user;
    }

    @Override
    public User update(long id, User user) throws NotFoundException {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " несуществует");
        }

        user.setId(id);
        User prevUser = users.get(id);
        if (user.getName() != null) {
            prevUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (emails.contains(user.getEmail())) {
                throw new RuntimeException("Email: " + user.getEmail() + " уже используется");
            }
            emails.remove(prevUser.getEmail());
            emails.add(user.getEmail());
            prevUser.setEmail(user.getEmail());
        }
        log.info("Пользователь с id={} обновлен", user.getId());
        return users.get(id);
    }

    @Override
    public void delete(long id) throws NotFoundException {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
        log.info("Пользователь с id={} удален", id);
    }
}
