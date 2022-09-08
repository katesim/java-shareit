package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Booking {
    Long id;
    String start; // дата и время начала бронирования;
    String end; // дата и время конца бронирования;
    Long item;// — вещь, которую пользователь бронирует;
    Long booker; // — пользователь, который осуществляет бронирование;
    Status status;
}
