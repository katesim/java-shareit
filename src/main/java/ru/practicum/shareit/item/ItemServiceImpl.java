package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId) {
        userService.getById(ownerId);
        return itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId);
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с id=" + id + " несуществует"));
    }

    @Override
    @Transactional
    public Item add(Item item) {
        userService.getById(item.getOwnerId());
        Item savedItem = itemRepository.save(item);
        log.info("Предмет с id={} создан", savedItem.getId());
        return savedItem;
    }

    @Override
    @Transactional
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
        itemRepository.save(prevItem);
        log.info("Предмет с id={} обновлен", prevItem.getId());
        return prevItem;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Item item = getById(id);
        itemRepository.delete(item);
        log.info("Предмет с id={} удален", id);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> collectedItems = new ArrayList<>();

        if (text.isBlank() || text.isEmpty()) {
            return collectedItems;
        }

        text = text.toLowerCase();

        collectedItems = itemRepository.search(text);
        return collectedItems;
    }

    @Override
    @Transactional
    public Comment addComment(Comment comment, List<Booking> authorBookings) {
        List<Long> itemsIds = authorBookings.stream().map(Booking::getItemId).collect(Collectors.toList());
        if (!itemsIds.contains(comment.getItemId())) {
            throw new ValidationException("Вы не бронировали данный предмет");
        }

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByItemIdOrderByIdAsc(Long itemId) {
        return commentRepository.getAllByItemIdOrderByIdAsc(itemId);
    }


}
