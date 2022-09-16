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
    private Long currId = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwner(Long owner) {
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
    public Item getById(Long id) throws NotFoundException {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
    }

    @Override
    public Item add(Item item) {
        userService.getById(item.getOwner()); // TODO так нормально валидировать существование пользователя?
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
        if (prevItem.getOwner() != item.getOwner()) {
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
    public void delete(Long id) throws NotFoundException {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id=" + id + " несуществует");
        }

        items.remove(id);
        log.info("Предмет с id={} удален", id);
    }

    @Override
    public List<Item> search(String text) {
        log.info(text.toLowerCase());
        List<Item> collectedItems = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return collectedItems;
        }

        for (Item item : items.values()) {
            if (! item.getAvailable()) {
                continue;
            }
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                collectedItems.add(item);
            }
        }
        return collectedItems;
    }
}
