package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    String name;
    String description;
    Boolean available; // — статус о том, доступна или нет вещь для аренды;
    Long request;
}
