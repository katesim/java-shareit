package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private String start;
    private String end;
    private Item item;
    private User booker;
    private Status status;


    @Data
    public static class User {
        private final Long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final Long id;
        private final String name;
        private final String description;
    }
}