package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(Long id);

    Page<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId, int from, int size);

    Item add(Item item);

    Item update(Item item);

    void delete(Long id);

    Page<Item> search(String text, int from, int size);

    Comment addComment(Comment comment, List<Booking> authorBookings);

    List<Comment> getAllCommentsByItemIdOrderByIdAsc(Long itemId);

    List<Item> getAllByRequestIdOrderByIdAsc(Long requestId);

}
