package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public List<Booking> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Booking> getAllByUserIdOrderByStartDesc(Long userId, State state) {
        List<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        userService.getById(userId);
        switch (state) {
            case ALL:
                bookings = repository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfterAndStatusEqualsOrderByStartDesc(
                        userId,
                        dateTime,
                        dateTime,
                        Status.APPROVED);
                break;
            case PAST:
                bookings = repository.findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        userId,
                        dateTime,
                        Status.APPROVED);
                break;
            case FUTURE:
                bookings = repository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId,
                        dateTime);
                break;
            case WAITING:
                bookings = repository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId,
                        Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId,
                        Status.REJECTED);
                break;
            default:
                throw new NotFoundException("Недопустимый статус");
        }

        return bookings;
    }

    @Override
    public List<Booking> getAllByOwnerIdOrderByStartDesc(Long ownerId, State state) {
        List<Long> ownerItems = itemService.getAllByOwnerIdOrderByIdAsc(ownerId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        userService.getById(ownerId);

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdInOrderByStartDesc(ownerItems);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartBeforeAndEndIsAfterAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        dateTime,
                        dateTime,
                        Status.APPROVED);
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        dateTime,
                        Status.APPROVED);
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartIsAfterOrderByStartDesc(
                        ownerItems,
                        dateTime);
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        Status.REJECTED);
                break;
            default:
                throw new NotFoundException("Недопустимый статус");
        }

        return bookings;
    }

    @Override
    public Booking getById(Long id, Long userId) {
        Booking booking = repository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирование с id=" + id + " несуществует"));

        Item item = itemService.getById(booking.getItemId());

        if (!userId.equals(booking.getBookerId()) & !userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Просмотр бронирования доступно только автору или владельцу");
        }

        return booking;
    }

    @Override
    public List<Booking> getByItemId(Long itemId, Status status) {
        return repository.findByItemIdAndStatusEquals(itemId, status);
    }

    @Override
    public Booking add(Booking booking, Long userId) {
        Item item = itemService.getById(booking.getItemId());
        LocalDateTime currDatetime = LocalDateTime.now();
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет с id=" + booking.getItemId() + " недоступен для бронирования");
        }

        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Владелец не может бронировать собственный предмет");
        }

        if (booking.getStart().isBefore(currDatetime)
                || booking.getEnd().isBefore(currDatetime)
                || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Недопустимое время брони");
        }

        userService.getById(booking.getBookerId());
        booking.setStatus(Status.WAITING);

        Booking savedBooking = repository.save(booking);
        log.info("Бронирование с id={} создано", savedBooking.getId());

        return savedBooking;
    }

    @Override
    public Booking updateStatus(Long id, Long userId, boolean approved) {
        Booking prevBooking = getById(id, userId);
        Item item = itemService.getById(prevBooking.getItemId());

        if (!userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Изменение статуса бронирования доступно только владельцу");
        }

        if (!prevBooking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Статус бронирования с id=" + id
                    + " уже проставлен: " + prevBooking.getStatus());
        }

        prevBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        Booking booking = repository.save(prevBooking);
        log.info("Статус бронирования с id={} обновлен на {}", prevBooking.getId(), prevBooking.getStatus());
        return booking;
    }

}
