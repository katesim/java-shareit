package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("{id}")
    public Item getById(@PathVariable Long id) throws NotFoundException {
        return itemService.getById(id);
    }

    @PostMapping()
    public Item create(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated(Create.class) @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        return itemService.add(item);
    }

    @PatchMapping("{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable Long id,
                       @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        item.setId(id);
        return itemService.update(item);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("search")
    public List<Item> getAll(@RequestParam String text) {
        return itemService.search(text);
    }

}
