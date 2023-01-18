package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingService {
    List<Booking> getAll();

    Booking getById(Long id, Long userId);

    List<Booking> getByItemId(Long itemId, Status status);

    Booking getLastBookingByItemId(Long itemId, Status status);

    Booking getNextBookingByItemId(Long itemId, Status status);

    Page<Booking> getAllByUserIdOrderByStartDesc(Long userId, State state, int from, int size);

    Page<Booking> getAllByOwnerIdOrderByStartDesc(Long ownerId, State state, int from, int size);

    Booking add(Booking booking);

    Booking updateStatus(Long id, Long userId, boolean approved);

}
