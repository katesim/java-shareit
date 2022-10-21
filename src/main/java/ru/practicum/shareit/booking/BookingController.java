package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.markers.Create;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;


    @GetMapping("{id}")
    public BookingResponseDto getById(@PathVariable long id,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingMapper.toBookingResponseDto(bookingService.getById(id, userId));
    }

    @GetMapping()
    public List<BookingResponseDto> getAllByUserId(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("{\"error\": \"Unknown state: " + state + "\" }");
        }

        return bookingService.getAllByUserIdOrderByStartDesc(
                        userId,
                        stateEnum)
                .stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("{\"error\": \"Unknown state: " + state + "\" }");
        }


        return bookingService.getAllByOwnerIdOrderByStartDesc(
                        userId,
                        stateEnum)
                .stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Validated(Create.class) @RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setStatus(Status.WAITING);
        booking.setBookerId(userId);
        return bookingMapper.toBookingResponseDto(bookingService.add(booking, userId));
    }

    @PatchMapping("{id}")
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long id,
                                     @RequestParam() boolean approved) {
        return bookingMapper.toBookingResponseDto(bookingService.updateStatus(id, userId, approved));
    }

}
