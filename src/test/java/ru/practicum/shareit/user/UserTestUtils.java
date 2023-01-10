package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserTestUtils {

    public static final long USER_ID = 1L;

    public static User getDefaultUser() {
        return new User(USER_ID, "user1", "user1@user.ru");
    }

    public static List<User> generateUsers(final int count) {
        List<User> users = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            final User user = new User(i, "user" + i, "user" + i + "@user.ru");
            users.add(user);
        }

        return users;
    }
}
