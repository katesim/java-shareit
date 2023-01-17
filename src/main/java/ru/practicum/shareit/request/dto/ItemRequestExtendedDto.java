package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestExtendedDto {
    @Positive
    private Long id;
    @NotBlank()
    private String description;
    private String created;
    private List<ItemDto> items;

    @Data
    public static class ItemDto {
        @Positive
        private final Long id;
        private final String name;
        private final String description;
        private final boolean available;
        @Positive
        private final Long requestId;
    }
}
