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
    private long currId = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwner(long owner) {
        userService.getById(owner); // TODO так нормально валидировать существование пользователя?

        List<Item> collectedItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == owner) {
                collectedItems.add(item);
            }
        }
        return collectedItems;
    }

    @Override
    public Item getById(long id) throws NotFoundException {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }

        return items.get(id);
    }

    @Override
    public Item add(Item item) {
        userService.getById(item.getOwner());
        item.setId(++currId);
        items.put(currId, item);
        log.info("Предмет с id={} создан", item.getId());
        return item;
    }

    @Override
    public Item update(Item item) throws NotFoundException {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Предмет с id=" + item.getId() + " несуществует");
        }

        Item prevItem = items.get(item.getId());

        if (item.getOwner() != prevItem.getOwner()) {
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
        log.info("Предмет с id={} обновлен", prevItem.getId());
        return prevItem;
    }

    @Override
    public void delete(long id) throws NotFoundException {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id=" + id + " несуществует");
        }

        items.remove(id);
        log.info("Предмет с id={} удален", id);
    }

    @Override
    public List<Item> search(String text) {
        text = text.toLowerCase();
        List<Item> collectedItems = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return collectedItems;
        }

        for (Item item : items.values()) {
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
