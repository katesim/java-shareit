package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;

public class CommentTestUtils {

    public static final long COMMENT_ID = 1L;
    public static final long ITEM_ID = 1L;
    public static final long USER_ID = 1L;

    public static Comment getDefaultComment() {
        return new Comment(COMMENT_ID, "comment", ITEM_ID, USER_ID, null);
    }
}
