package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    List<Booking> getAll();

    Booking getById(Long id, Long userId);

    List<Booking> getByItemId(Long itemId, Status status);

    Booking getLastBookingByItemId(Long itemId, Status status);

    Booking getNextBookingByItemId(Long itemId, Status status);

    List<Booking> getAllByUserIdOrderByStartDesc(Long userId, State state);

    List<Booking> getAllByOwnerIdOrderByStartDesc(Long ownerId, State state);

    Booking add(Booking booking, Long userId);

    Booking updateStatus(Long id, Long userId, boolean approved);

}
