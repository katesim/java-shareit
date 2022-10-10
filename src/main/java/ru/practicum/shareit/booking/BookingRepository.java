package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatusEquals(Long bookerId, Status status, Sort sort);

    List<Booking> findByBookerIdAndEndIsBeforeAndStatusEquals(
            Long bookerId, LocalDateTime end, Status status,
            Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(
            Long bookerId, LocalDateTime start,
            Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterAndStatusEquals(
            Long bookerId, LocalDateTime start, LocalDateTime end, Status status,
            Sort sort);

    List<Booking> findByItemIdIn(List<Long> itemIds, Sort sort);

    List<Booking> findByItemIdInAndStatusEquals(List<Long> itemIds, Status status, Sort sort);

    List<Booking> findByItemIdInAndEndIsBeforeAndStatusEquals(
            List<Long> itemIds, LocalDateTime end, Status status,
            Sort sort);

    List<Booking> findByItemIdInAndStartIsAfter(
            List<Long> itemIds, LocalDateTime start,
            Sort sort);

    List<Booking> findByItemIdInAndStartBeforeAndEndIsAfterAndStatusEquals(
            List<Long> itemIds, LocalDateTime start, LocalDateTime end, Status status,
            Sort sort);

}
