package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public class UserTestUtils {

    public static final long USER_ID = 1L;

    public static User getDefaultUser() {
        return new User(USER_ID, "user1", "user1@user.ru");
    }
}
