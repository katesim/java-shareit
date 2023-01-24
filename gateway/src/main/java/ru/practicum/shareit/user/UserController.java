package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        return client.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        return client.create(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                          @Validated(Update.class) @RequestBody UserDto userDto) {
        return client.update(id, userDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        client.delete(id);
    }

}
