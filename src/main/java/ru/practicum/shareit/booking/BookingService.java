package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookingService {
    List<Booking> getAll();

    Booking getById(Long id, Long userId);

    List<Booking> getByItemId(Long itemId, Status status);

    List<Booking> getAllByUserId(Long userId, State state, Sort sort);

    List<Booking> getAllByOwnerId(Long ownerId, State state, Sort sort);

    Booking add(Booking booking, Long userId);

    Booking updateStatus(Long id, Long userId, boolean approved);

}
