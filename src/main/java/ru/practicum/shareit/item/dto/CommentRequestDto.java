package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequestDto {
    @NotBlank(groups = Create.class)
    private String text;
}
