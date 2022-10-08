package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable long id) throws NotFoundException {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.add(user));
    }

    @PatchMapping("{id}")
    public UserDto update(@PathVariable long id,
                       @Validated(Update.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.update(id, user));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

}
