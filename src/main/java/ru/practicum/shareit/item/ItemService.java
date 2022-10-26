package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(Long id);

    List<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId);

    Item add(Item item);

    Item update(Item item);

    void delete(Long id);

    List<Item> search(String text);

    Comment addComment(Comment comment, List<Booking> authorBookings);

    List<Comment> getAllCommentsByItemIdOrderByIdAsc(Long itemId);

    List<Item> getAllByRequestIdOrderByIdAsc(Long requestId);

}
