package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping
    public List<ItemExtendedDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                        @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {

        List<ItemExtendedDto> ownersItemsWithBookingsDto = new ArrayList<>();

        for (Item item : itemService.getAllByOwnerIdOrderByIdAsc(userId, from, size)) {
            ItemExtendedDto itemDto = ItemMapper.toItemExtendedDto(item);

            Booking next = bookingService.getNextBookingByItemId(itemDto.getId(), Status.APPROVED);
            Booking last = bookingService.getLastBookingByItemId(itemDto.getId(), Status.APPROVED);

            itemDto.setLastBooking(last);
            itemDto.setNextBooking(next);

            List<Comment> comments = itemService.getAllCommentsByItemIdOrderByIdAsc(itemDto.getId());

            List<ItemExtendedDto.CommentDto> commentsDtos = comments
                    .stream()
                    .map(c -> ItemMapper.toCommentDto(c, userService.getById(c.getId())))
                    .collect(Collectors.toList());

            itemDto.setComments(commentsDtos);

            ownersItemsWithBookingsDto.add(itemDto);
        }

        return ownersItemsWithBookingsDto;
    }

    @GetMapping("{id}")
    public ItemExtendedDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long id) {
        Item item = itemService.getById(id);
        ItemExtendedDto itemDto = ItemMapper.toItemExtendedDto(item);
        if (userId == item.getOwnerId()) {
            Booking next = bookingService.getNextBookingByItemId(itemDto.getId(), Status.APPROVED);
            Booking last = bookingService.getLastBookingByItemId(itemDto.getId(), Status.APPROVED);

            itemDto.setLastBooking(last);
            itemDto.setNextBooking(next);
        }

        List<Comment> comments = itemService.getAllCommentsByItemIdOrderByIdAsc(itemDto.getId());

        List<ItemExtendedDto.CommentDto> commentsDtos = comments
                .stream()
                .map(c -> ItemMapper.toCommentDto(c, userService.getById(c.getId())))
                .collect(Collectors.toList());


        itemDto.setComments(commentsDtos);

        return itemDto;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemService.add(item));
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setId(id);
        return ItemMapper.toItemDto(itemService.update(item));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        return itemService.search(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("{itemId}/comment")
    public ItemExtendedDto.CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Validated(Create.class) @RequestBody CommentRequestDto commentDto) {
        Comment comment = ItemMapper.toComment(itemId, userId, commentDto);
        User author = userService.getById(userId);
        List<Booking> authorBookings = bookingService.getAllByUserIdOrderByStartDesc(
                userId, State.PAST, 0, Integer.MAX_VALUE).toList();

        return ItemMapper.toCommentDto(itemService.addComment(comment, authorBookings), author);
    }
}
