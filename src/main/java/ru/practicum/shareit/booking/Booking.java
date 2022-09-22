package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Booking {
    private long id;
    private String start;
    private String end;
    private long item;
    private long booker;
    private Status status;
}
