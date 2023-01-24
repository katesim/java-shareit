package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            Long bookerId, LocalDateTime end, Status status, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdAndStatusEquals(Long itemId, Status status);

    Page<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds, Pageable pageable);

    Page<Booking> findByItemIdInAndStatusEqualsOrderByStartDesc(List<Long> itemIds, Status status, Pageable pageable);

    Page<Booking> findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            List<Long> itemIds, LocalDateTime end, Status status, Pageable pageable);

    Page<Booking> findByItemIdInAndStartIsAfterOrderByStartDesc(
            List<Long> itemIds, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
            List<Long> itemIds, LocalDateTime start, LocalDateTime end, Pageable pageable);

}
