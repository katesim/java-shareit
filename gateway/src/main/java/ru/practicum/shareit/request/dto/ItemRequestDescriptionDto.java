package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDescriptionDto {
    @Positive
    private long id;

    @NotBlank(groups = Create.class)
    private String description;
}
