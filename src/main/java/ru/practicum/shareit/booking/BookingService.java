package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    List<Booking> getAll();

    Booking getById(Long id, Long userId);

    List<Booking> getAllByUserId(Long userId, State state);

    List<Booking> getAllByOwnerId(Long ownerId, State state);

    Booking add(Booking booking, Long userId);

    Booking updateStatus(Long id, Long userId, boolean approved);

}
