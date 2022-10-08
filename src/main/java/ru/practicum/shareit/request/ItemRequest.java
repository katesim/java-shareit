package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemRequest {
    private long id;
    private String description;
    private long requestor;
    private String created;
}
