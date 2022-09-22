package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(long id);

    List<Item> getAllByOwner(long owner);

    Item add(Item item);

    Item update(Item item);

    void delete(long id);

    List<Item> search(String text);
}
