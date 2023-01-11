package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequesExtendedtDto;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated().toString()
        );
    }

    public static ItemRequesExtendedtDto toItemRequestExtendedtDto(ItemRequest request, List<Item> items) {
        return new ItemRequesExtendedtDto(
                request.getId(),
                request.getDescription(),
                request.getCreated().toString(),
                items.stream().map(ItemRequestMapper::toItemDto).collect(Collectors.toList())
        );
    }

    private static ItemRequesExtendedtDto.ItemDto toItemDto(Item item) {
        return new ItemRequesExtendedtDto.ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDescriptionDto descriptionDto, Long requesterId) {
        ItemRequest request = new ItemRequest();
        request.setRequestorId(requesterId);
        request.setDescription(descriptionDto.getDescription());
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, Long requesterId) {
        ItemRequest request = new ItemRequest();
        request.setRequestorId(requesterId);
        request.setDescription(requestDto.getDescription());
        request.setCreated(LocalDateTime.now());
        return request;
    }

}
