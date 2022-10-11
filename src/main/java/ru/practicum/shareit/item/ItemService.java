package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
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
    ItemWithBookingsDto setBookings(ItemWithBookingsDto itemDto, List<Booking> bookings);

}
