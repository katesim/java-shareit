package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public Page<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId, int from, int size) {
        checkUserExistence(ownerId);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId, pageable);
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с id=" + id + " несуществует"));
    }

    @Override
    @Transactional
    public Item add(Item item) {
        checkUserExistence(item.getOwnerId());
        Item savedItem = itemRepository.save(item);
        log.info("Предмет с id={} создан", savedItem.getId());
        return savedItem;
    }

    @Override
    @Transactional
    public Item update(Item item) {
        Item prevItem = getById(item.getId());

        if (!Objects.equals(item.getOwnerId(), prevItem.getOwnerId())) {
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
    public Page<Item> search(String text, int from, int size) {
        if (text.isBlank() || text.isEmpty()) {
            return Page.empty();
        }

        text = text.toLowerCase();

        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.search(text, pageable);
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

    @Override
    public List<Item> getAllByRequestIdOrderByIdAsc(Long requestId) {
        return itemRepository.getAllByRequestIdOrderByIdAsc(requestId);
    }


    private void checkUserExistence(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " несуществует"));
    }

}
