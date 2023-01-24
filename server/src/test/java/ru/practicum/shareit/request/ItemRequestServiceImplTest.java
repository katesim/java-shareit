package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.ItemRequestTestUtils.generateRequests;
import static ru.practicum.shareit.request.ItemRequestTestUtils.getDefaultRequest;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private static final int PAGE_START_FROM = 0;
    private static final int PAGE_SIZE_DEFAULT = 10;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl subject;

    @Test
    void add() {
        ItemRequest itemRequest = getDefaultRequest();
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequest result = subject.add(itemRequest);

        assertEquals(itemRequest, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(itemRequest);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void addWhenUserNotExists() {
        ItemRequest itemRequest = getDefaultRequest();
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subject.add(itemRequest));
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getById() {
        ItemRequest itemRequest = getDefaultRequest();
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest result = subject.getById(user.getId(), itemRequest.getId());

        assertEquals(itemRequest, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getByIdWhenUserNotExists() {
        ItemRequest itemRequest = getDefaultRequest();
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subject.getById(user.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getByIdWhenItemRequestNotExists() {
        ItemRequest itemRequest = getDefaultRequest();
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subject.getById(user.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getAllByRequesterId() {
        List<ItemRequest> itemRequests = generateRequests(3);
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.getAllByRequesterIdOrderByCreatedAsc(user.getId()))
                .thenReturn(itemRequests);

        List<ItemRequest> result = subject.getAllByRequesterId(user.getId());

        assertEquals(itemRequests, result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1))
                .getAllByRequesterIdOrderByCreatedAsc(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getAllByRequesterIdWhenUserNotExists() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getAllByRequesterId(user.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getExistedForUserId() {
        List<ItemRequest> itemRequests = generateRequests(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.getAllByRequesterIdNotOrderByCreatedAsc(user.getId(), pageable))
                .thenReturn(new PageImpl<>(itemRequests));

        Page<ItemRequest> result = subject.getExistedForUserId(user.getId(), PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(itemRequests, result.toList());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1))
                .getAllByRequesterIdNotOrderByCreatedAsc(user.getId(), pageable);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }

    @Test
    void getExistedForUserIdWhenUserNotExists() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getExistedForUserId(user.getId(), PAGE_START_FROM, PAGE_SIZE_DEFAULT));

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRequestRepository);
    }
}
