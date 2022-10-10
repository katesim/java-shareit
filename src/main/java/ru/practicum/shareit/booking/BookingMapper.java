package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemService itemService;
    private final UserService userService;

    public BookingResponseDto toBookingResponseDto(Booking booking) {
        Item item = itemService.getById(booking.getItemId());
        User booker = userService.getById(booking.getBookerId());
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                new BookingResponseDto.Item(item.getId(), item.getName(), item.getDescription()),
                new BookingResponseDto.User(booker.getId(), booker.getName()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse(bookingRequestDto.getStart()));
        booking.setEnd(LocalDateTime.parse(bookingRequestDto.getEnd()));
        booking.setItemId(bookingRequestDto.getItemId());
        booking.setBookerId(bookingRequestDto.getBookerId());
        booking.setStatus(bookingRequestDto.getStatus());
        return booking;
    }
}
