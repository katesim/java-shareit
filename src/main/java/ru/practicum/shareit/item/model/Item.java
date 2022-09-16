package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@SuperBuilder
public class Item {
    long id;
    String name;
    String description;
    Boolean available;
    long owner;
    ItemRequest request;
}
