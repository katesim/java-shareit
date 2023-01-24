package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.Min;

import java.util.ArrayList;

import static ru.practicum.shareit.common.ShareItConstants.PAGE_SIZE_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.PAGE_START_FROM_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        return client.getAll(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long id) {

        return client.getById(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return client.create(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        return client.update(id, userId, itemDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        client.delete(id);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        if (text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return client.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @Validated(Create.class) @RequestBody CommentRequestDto commentDto) {
        return client.addComment(itemId, userId, commentDto);
    }
}
