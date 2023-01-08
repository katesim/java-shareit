package ru.practicum.shareit.booking;

import java.time.LocalDateTime;


public class BookingTestUtils {

    public static final long BOOKING_ID = 1L;
    public static final LocalDateTime BOOKING_START = LocalDateTime.of(2022, 12, 1, 14, 00);
    public static final LocalDateTime BOOKING_END = LocalDateTime.of(2022, 12, 1, 18, 00);
    public static final long ITEM_ID = 1L;
    public static final long USER_ID = 1L;

    public static Booking getDefaultBooking() {
        return new Booking(BOOKING_ID, BOOKING_START, BOOKING_END, ITEM_ID, USER_ID, Status.APPROVED);
    }
}
