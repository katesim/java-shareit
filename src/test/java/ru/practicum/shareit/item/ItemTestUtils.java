package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemTestUtils {

    public static final long ITEM_ID = 1L;
    public static final long USER_ID = 1L;
    public static final long REQUEST_ID = 1L;

    public static Item getDefaultItem() {
        return new Item(ITEM_ID, "item1", "description1", true, USER_ID, REQUEST_ID);
    }

    public static List<Item> generateItems(final int count) {
        List<Item> items = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            final Item item = new Item(i, "item" + i, "description" + i, true, USER_ID, REQUEST_ID);
            items.add(item);
        }

        return items;
    }
}
