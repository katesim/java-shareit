package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceInMemory implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public List<Item> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Item> getAllByOwner(Long ownerId) {
        userService.getById(ownerId);

        List<Item> itemsByOwners = new ArrayList<>();
        for (Item item : getAll()) { // TODO заменить на запрос к БД
            if (item.getOwnerId() == ownerId) {
                itemsByOwners.add(item);
            }
        }
        return itemsByOwners;
    }

    @Override
    public Item getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с id=" + id + " несуществует"));
    }

    @Override
    public Item add(Item item) {
        userService.getById(item.getOwnerId());
        Item savedItem = repository.save(item);
        log.info("Предмет с id={} создан", savedItem.getId());
        return savedItem;
    }

    @Override
    public Item update(Item item) {
        Item prevItem = getById(item.getId());

        if (item.getOwnerId() != prevItem.getOwnerId()) {
            throw new ForbiddenException("Изменение предмета доступно только владельцу");
        }
        if (item.getName() != null) {
            prevItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            prevItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            prevItem.setAvailable(item.getAvailable());
        }
        repository.save(prevItem);
        log.info("Предмет с id={} обновлен", prevItem.getId());
        return prevItem;
    }

    @Override
    public void delete(Long id) {
        Item item = getById(id);
        repository.delete(item);
        log.info("Предмет с id={} удален", id);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> collectedItems = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return collectedItems;
        }

        text = text.toLowerCase();
        for (Item item : getAll()) { // TODO заменить на запрос к БД
            if (!item.getAvailable()) {
                continue;
            }
            if (item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text)) {
                collectedItems.add(item);
            }
        }
        return collectedItems;
    }
}
