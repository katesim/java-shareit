package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Create;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public List<ItemWithBookingsDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemWithBookingsDto> ownersItemsWithBookingsDto = new ArrayList<>();

        for (Item item : itemService.getAllByOwner(userId)) {
            ItemWithBookingsDto itemDto = ItemMapper.toItemWithBookingsDto(item, null, null);
            List<Booking> bookings = bookingService.getByItemId(itemDto.getId());
            itemService.setBookings(itemDto, bookings);
            ownersItemsWithBookingsDto.add(itemDto);
        }

        return ownersItemsWithBookingsDto;
    }

    @GetMapping("{id}")
    public ItemWithBookingsDto getById(@PathVariable long id) throws NotFoundException {
        ItemWithBookingsDto itemDto = ItemMapper.toItemWithBookingsDto(itemService.getById(id), null, null);
        List<Booking> bookings = bookingService.getByItemId(itemDto.getId());
        itemService.setBookings(itemDto, bookings);
        return itemDto;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        return ItemMapper.toItemDto(itemService.add(item));
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        item.setId(id);
        return ItemMapper.toItemDto(itemService.update(item));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }

    @GetMapping("search")
    public List<ItemDto> getAll(@RequestParam String text) {
        return itemService.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
