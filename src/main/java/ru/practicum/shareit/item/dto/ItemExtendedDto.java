package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ItemExtendedDto {
    @Positive
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;
    @Positive
    private Long request;

    @Data
    public static class CommentDto {
        @Positive
        private final Long id;
        private final String text;
        private final String authorName;
        private final LocalDateTime created;
    }

}
