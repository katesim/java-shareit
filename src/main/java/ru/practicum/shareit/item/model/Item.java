package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@SuperBuilder
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
