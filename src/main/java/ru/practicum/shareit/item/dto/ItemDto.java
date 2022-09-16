package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    @NotBlank(groups = Create.class)
    String name;
    @NotBlank(groups = Create.class)
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
    long request;
}
