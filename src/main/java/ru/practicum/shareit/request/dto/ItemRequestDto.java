package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    @Positive
    private Long id;
    @NotBlank()
    private String description;
    private String created;

}

