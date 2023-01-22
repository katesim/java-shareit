package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.UserTestUtils.USER_ID;
import static ru.practicum.shareit.user.UserTestUtils.generateUsers;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl subject;

    @Test
    void testGetAll() {
        List<User> users = generateUsers(10);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = subject.getAll();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetByIdWhenUserExistsShouldReturnUser() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = subject.getById(user.getId());

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGetByIdWhenUserNotExistsShouldThrow() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getById(USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testAdd() {
        User user = getDefaultUser();
        when(userRepository.save(user)).thenReturn(user);

        User result = subject.add(user);

        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdate() {
        User user = getDefaultUser();
        User updatedUser = getDefaultUser().toBuilder()
                .name("NEW")
                .email("new@email.com")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = subject.update(user.getId(), updatedUser);

        assertEquals(updatedUser, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testDelete() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        subject.delete(user.getId());

        verify(userRepository, times(1)).delete(user);
    }
}
