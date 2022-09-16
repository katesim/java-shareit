package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Booking {
    long id;
    String start;
    String end;
    long item;
    long booker;
    Status status;
}
