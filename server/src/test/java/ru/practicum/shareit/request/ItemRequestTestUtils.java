package ru.practicum.shareit.request;

import com.jayway.jsonpath.JsonPath;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ItemRequestTestUtils {

    public static final Long REQUEST_ID = 1L;
    public static final Long REQUESTER_ID = 1L;
    public static final String REQUEST_DESCRIPTION = "description";
    public static final LocalDateTime REQUEST_CREATED = LocalDateTime.of(2022, 11, 15, 14, 00);

    public static ItemRequest getDefaultRequest() {
        return new ItemRequest(REQUEST_ID, REQUEST_DESCRIPTION, REQUESTER_ID, REQUEST_CREATED);
    }

    public static List<ItemRequest> generateRequests(final int count) {
        List<ItemRequest> itemRequests = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            final ItemRequest booking = new ItemRequest(i, REQUEST_DESCRIPTION + i, REQUESTER_ID, REQUEST_CREATED);
            itemRequests.add(booking);
        }

        return itemRequests;
    }

    public static void assertItemRequestAtIndex(
            final String response, final List<ItemRequest> itemRequests, List<Item> items, final int index) {
        assertThat(JsonPath.read(response, "$[" + index + "].id"),
                is(itemRequests.get(index).getId()));
        assertThat(JsonPath.read(response, "$[" + index + "].description"),
                is(itemRequests.get(index).getDescription()));
        assertThat(JsonPath.read(response, "$[" + index + "].created"),
                is(itemRequests.get(index).getCreated().toString()));

        for (int idx = 0; idx < items.size(); idx++) {
            assertThat(JsonPath.read(response, "$[" + index + "].items.[" + idx + "].id"),
                    is(items.get(idx).getId()));
            assertThat(JsonPath.read(response, "$[" + index + "].items.[" + idx + "].name"),
                    is(items.get(idx).getName()));
            assertThat(JsonPath.read(response, "$[" + index + "].items.[" + idx + "].description"),
                    is(items.get(idx).getDescription()));
            assertThat(JsonPath.read(response, "$[" + index + "].items.[" + idx + "].available"),
                    is(items.get(idx).getAvailable()));
            assertThat(JsonPath.read(response, "$[" + index + "].items.[" + idx + "].requestId"),
                    is(items.get(idx).getRequestId()));
        }
    }

    public static void assertItemAtIndex(final String response, final List<Item> items, final int index) {
        assertThat(JsonPath.read(response, "$.items.[" + index + "].id"),
                is(items.get(index).getId()));
        assertThat(JsonPath.read(response, "$.items.[" + index + "].name"),
                is(items.get(index).getName()));
        assertThat(JsonPath.read(response, "$.items.[" + index + "].description"),
                is(items.get(index).getDescription()));
        assertThat(JsonPath.read(response, "$.items.[" + index + "].available"),
                is(items.get(index).getAvailable()));
        assertThat(JsonPath.read(response, "$.items.[" + index + "].requestId"),
                is(items.get(index).getRequestId()));
    }
}
