package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.ValidationException;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User getById(@PathVariable Long id) throws NotFoundException {
        return userService.getById(id);
    }

    @PostMapping()
    public User create(@Validated(Create.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.add(user);
    }

    @PatchMapping("{id}")
    public User update(@PathVariable Long id,
                       @Validated(Update.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.update(id, user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
