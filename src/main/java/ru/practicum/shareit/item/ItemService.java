package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(Long id);

    List<Item> getAllByOwner(Long ownerId);

    Item add(Item item);

    Item update(Item item);

    void delete(Long id);

    List<Item> search(String text);
}
