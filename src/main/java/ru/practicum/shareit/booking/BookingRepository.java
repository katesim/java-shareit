package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            Long bookerId, LocalDateTime end, Status status);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterAndStatusEqualsOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Status status);

    List<Booking> findByItemIdAndStatusEquals(Long itemId, Status status);

    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItemIdInAndStatusEqualsOrderByStartDesc(List<Long> itemIds, Status status);

    List<Booking> findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            List<Long> itemIds, LocalDateTime end, Status status);

    List<Booking> findByItemIdInAndStartIsAfterOrderByStartDesc(
            List<Long> itemIds, LocalDateTime start);

    List<Booking> findByItemIdInAndStartBeforeAndEndIsAfterAndStatusEqualsOrderByStartDesc(
            List<Long> itemIds, LocalDateTime start, LocalDateTime end, Status status);

}
