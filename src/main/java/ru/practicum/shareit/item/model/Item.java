package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available; // — статус о том, доступна или нет вещь для аренды;
    Long owner; // владелец вещи
    ItemRequest request; // — если вещь была создана по запросу другого пользователя, то в этом
    // поле будет храниться ссылка на соответствующий запрос.
}