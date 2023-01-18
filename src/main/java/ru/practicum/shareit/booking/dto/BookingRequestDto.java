package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
public class BookingRequestDto {
    @Positive
    private long id;
    private String start;
    private String end;
    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;
    private Status status;

}
