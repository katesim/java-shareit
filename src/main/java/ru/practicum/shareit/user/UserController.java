package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.ValidationException;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private Long currId = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    @GetMapping("")
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @GetMapping("{id}")
    public User getById(@PathVariable Long id) throws NotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
    }

    @PostMapping()
    public User create(@RequestBody User user) throws ValidationException {
        UserValidator.validateEmail(user.getEmail(), emails);
        user.setId(++currId);
        users.put(currId, user);
        emails.add(user.getEmail());
        log.info("Пользователь с id={} создан", user.getId());
        return user;
    }

    @PatchMapping("{id}")
    public User update(@PathVariable Long id, @RequestBody User user) throws ValidationException {
        user.setId(id);
        if (users.containsKey(id)) {
            User prevUser = users.get(id);
            if (user.getName() != null) {
                prevUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                UserValidator.validateEmail(user.getEmail(), emails);
                emails.remove(prevUser.getEmail());
                emails.add(user.getEmail());
                prevUser.setEmail(user.getEmail());
            }
            log.info("Пользователь с id={} обновлен", user.getId());
        } else {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " несуществует");
        }
        return users.get(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        if (users.containsKey(id)) {
            emails.remove(users.get(id).getEmail());
            users.remove(id);
            log.info("Пользователь с id={} удален", id);
        } else {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
    }

}
