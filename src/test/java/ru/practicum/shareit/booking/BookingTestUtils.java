package ru.practicum.shareit.booking;

import com.jayway.jsonpath.JsonPath;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class BookingTestUtils {

    public static final long BOOKING_ID = 1L;
    public static final LocalDateTime BOOKING_START = LocalDateTime.of(2022, 12, 1, 14, 00);
    public static final LocalDateTime BOOKING_END = LocalDateTime.of(2022, 12, 1, 18, 00);
    public static final long ITEM_ID = 1L;
    public static final long USER_ID = 1L;

    public static Booking getDefaultBooking() {
        return new Booking(BOOKING_ID, BOOKING_START, BOOKING_END, ITEM_ID, USER_ID, Status.APPROVED);
    }

    public static List<Booking> generateBookings(final int count) {
        List<Booking> bookings = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            final Booking booking = new Booking(i, BOOKING_START, BOOKING_END, ITEM_ID, USER_ID, Status.APPROVED);
            bookings.add(booking);
        }

        return bookings;
    }

    public static void assertBookingAtIndex(
            final String json, final List<Booking> bookings, final User user, final Item item, final int index) {

        assertThat(JsonPath.read(json, "$[" + index + "].id"),
                is(bookings.get(index).getId()));
        assertThat(JsonPath.read(json, "$[" + index + "].start"),
                is(bookings.get(index).getStart().toString()));
        assertThat(JsonPath.read(json, "$[" + index + "].end"),
                is(bookings.get(index).getEnd().toString()));
        assertThat(JsonPath.read(json, "$[" + index + "].status"),
                is(bookings.get(index).getStatus().name()));

        assertThat(JsonPath.read(json, "$[" + index + "].item.id"),
                is(item.getId()));
        assertThat(JsonPath.read(json, "$[" + index + "].item.name"),
                is(item.getName()));
        assertThat(JsonPath.read(json, "$[" + index + "].item.description"),
                is(item.getDescription()));

        assertThat(JsonPath.read(json, "$[" + index + "].booker.id"),
                is(user.getId()));
        assertThat(JsonPath.read(json, "$[" + index + "].booker.name"),
                is(user.getName()));
    }
}
