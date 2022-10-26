package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemRequesExtendedtDto {
    private Long id;
    @NotBlank()
    private String description;
    private String created;
    private List<ItemDto> items;

    @Data
    public static class ItemDto {
        private final Long id;
        private final String name;
        private final String description;
        private final boolean available;
        private final Long requestId;
    }
}
