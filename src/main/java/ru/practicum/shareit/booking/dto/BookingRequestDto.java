package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

@Data
@Builder
@AllArgsConstructor
public class BookingRequestDto {
    private long id;
    private String start;
    private String end;
    private Long itemId;
    private Long bookerId;
    private Status status;

}
