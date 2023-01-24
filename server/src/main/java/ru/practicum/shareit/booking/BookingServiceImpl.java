package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<Booking> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<Booking> getAllByUserIdOrderByStartDesc(Long userId, State state, int from, int size) {
        Page<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        checkUserExistence(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (state) {
            case ALL:
                bookings = repository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId,
                        dateTime,
                        dateTime,
                        pageable);
                break;
            case PAST:
                bookings = repository.findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        userId,
                        dateTime,
                        Status.APPROVED,
                        pageable);
                break;
            case FUTURE:
                bookings = repository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId,
                        dateTime,
                        pageable);
                break;
            case WAITING:
                bookings = repository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId,
                        Status.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = repository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId,
                        Status.REJECTED,
                        pageable);
                break;
            default:
                throw new NotFoundException("Недопустимый статус");
        }

        return bookings;
    }

    @Override
    public Page<Booking> getAllByOwnerIdOrderByStartDesc(Long ownerId, State state, int from, int size) {
        checkUserExistence(ownerId);

        List<Long> ownerItems = itemRepository.getAllByOwnerIdOrderByIdAsc(ownerId, PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime dateTime = LocalDateTime.now();
        Page<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdInOrderByStartDesc(ownerItems, pageable);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
                        ownerItems,
                        dateTime,
                        dateTime,
                        pageable);
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        dateTime,
                        Status.APPROVED,
                        pageable);
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartIsAfterOrderByStartDesc(
                        ownerItems,
                        dateTime,
                        pageable);
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        Status.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItems,
                        Status.REJECTED,
                        pageable);
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

        Item item = getItem(booking.getItemId());

        if (!userId.equals(booking.getBookerId()) && !userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Просмотр бронирования доступно только автору или владельцу");
        }

        return booking;
    }

    @Override
    public List<Booking> getByItemId(Long itemId, Status status) {
        return repository.findByItemIdAndStatusEquals(itemId, status);
    }

    @Override
    public Booking getLastBookingByItemId(Long itemId, Status status) {
        List<Booking> bookings = getByItemId(itemId, status);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        if (!bookings.isEmpty()) {
            Booking last = bookings.get(0);
            for (Booking b : bookings) {
                if (b.getEnd().isBefore(now) && b.getEnd().isAfter(last.getEnd())) last = b;
            }
            lastBooking = last;
        }
        return lastBooking;
    }

    @Override
    public Booking getNextBookingByItemId(Long itemId, Status status) {
        List<Booking> bookings = getByItemId(itemId, status);
        LocalDateTime now = LocalDateTime.now();

        Booking nextBooking = null;
        if (!bookings.isEmpty()) {
            Booking next = bookings.get(bookings.size() - 1);
            for (Booking b : bookings) {
                if (b.getStart().isAfter(now) && b.getStart().isBefore(next.getStart())) next = b;
            }
            nextBooking = next;
        }
        return nextBooking;
    }

    @Override
    @Transactional
    public Booking add(Booking booking) {
        Item item = getItem(booking.getItemId());
        LocalDateTime currDatetime = LocalDateTime.now();
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет с id=" + booking.getItemId() + " недоступен для бронирования");
        }

        checkUserExistence(booking.getBookerId());
        if (booking.getBookerId().equals(item.getOwnerId())) {
            throw new NotFoundException("Владелец не может бронировать собственный предмет");
        }

        if (booking.getStart().isBefore(currDatetime)
                || booking.getEnd().isBefore(currDatetime)
                || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Недопустимое время брони");
        }

        booking.setStatus(Status.WAITING);
        Booking savedBooking = repository.save(booking);
        log.info("Бронирование с id={} создано", savedBooking.getId());

        return savedBooking;
    }

    @Override
    @Transactional
    public Booking updateStatus(Long id, Long userId, boolean approved) {
        Booking prevBooking = getById(id, userId);
        Item item = getItem(prevBooking.getItemId());

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

    private void checkUserExistence(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " несуществует"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id=" + itemId + " несуществует"));
    }

}
