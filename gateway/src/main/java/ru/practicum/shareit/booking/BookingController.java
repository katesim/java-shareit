package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.Min;

import static ru.practicum.shareit.common.ShareItConstants.PAGE_SIZE_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.PAGE_START_FROM_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient client;

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(
            @PathVariable long id,
            @RequestHeader(USER_ID_HEADER) long userId) {

        return client.getById(id, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByUserId(
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("{\"error\": \"Unknown state: " + state + "\" }");
        }

        return client.getAllByUserId(userId, stateEnum, from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllByOwnerId(
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("{\"error\": \"Unknown state: " + state + "\" }");
        }

        return client.getAllByOwnerId(userId, stateEnum, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Validated(Create.class) @RequestBody BookingRequestDto bookingRequestDto) {

        return client.create(userId, bookingRequestDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable long id,
                                     @RequestParam() boolean approved) {
        return client.update(id, userId, approved);
    }
}
