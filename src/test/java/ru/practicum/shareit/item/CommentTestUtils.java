package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentTestUtils {

    public static final long COMMENT_ID = 1L;
    public static final long ITEM_ID = 1L;
    public static final long USER_ID = 1L;

    public static Comment getDefaultComment() {
        return new Comment(COMMENT_ID, "comment", ITEM_ID, USER_ID, null);
    }

    public static List<Comment> generateComments(final int count) {
        List<Comment> comments = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            final Comment comment = new Comment(i, "comment" + i, ITEM_ID, USER_ID, null);
            comments.add(comment);
        }

        return comments;
    }
}
