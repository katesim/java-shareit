package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(Long id) throws NotFoundException;

    List<Item> getAllByOwner(Long owner) throws NotFoundException;

    Item add(Item item);

    Item update(Item item) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    List<Item> search(String text);
}
