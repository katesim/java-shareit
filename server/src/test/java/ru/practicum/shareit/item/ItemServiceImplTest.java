package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingTestUtils.getDefaultBooking;
import static ru.practicum.shareit.item.CommentTestUtils.generateComments;
import static ru.practicum.shareit.item.CommentTestUtils.getDefaultComment;
import static ru.practicum.shareit.item.ItemTestUtils.generateItems;
import static ru.practicum.shareit.item.ItemTestUtils.getDefaultItem;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private static final long ITEM_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 1L;
    private static final int PAGE_SIZE = 3;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl subject;

    @Test
    void testGetAll() {
        List<Item> items = generateItems(10);
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = subject.getAll();

        assertThat(result, is(items));
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetByIdWhenItemExistsShouldReturn() {
        Item item = getDefaultItem();
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        Item result = subject.getById(ITEM_ID);

        assertThat(result, is(item));
        verify(itemRepository, times(1)).findById(ITEM_ID);
    }

    @Test
    void testGetByIdWhenItemNotExistsShouldThrow() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subject.getById(ITEM_ID));
        verify(itemRepository, times(1)).findById(ITEM_ID);
    }

    @Test
    void testGetAllByOwnerIdWhenUserExistsShouldReturn() {
        List<Item> items = generateItems(PAGE_SIZE);
        User owner = getDefaultUser();
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));

        Page<Item> result = subject.getAllByOwnerIdOrderByIdAsc(USER_ID, 0, PAGE_SIZE);

        assertEquals(result.toList(), items);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(itemRepository, times(1))
                .getAllByOwnerIdOrderByIdAsc(eq(USER_ID), eq(pageable));
    }

    @Test
    void testGetAllByOwnerIdWhenUserExistsShouldThrow() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getAllByOwnerIdOrderByIdAsc(USER_ID, 0, PAGE_SIZE));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testGetAllCommentsByItemId() {
        List<Comment> comments = generateComments(10);
        when(commentRepository.getAllByItemIdOrderByIdAsc(ITEM_ID))
                .thenReturn(comments);

        List<Comment> result = subject.getAllCommentsByItemIdOrderByIdAsc(ITEM_ID);

        assertEquals(result, comments);
        verify(commentRepository, times(1))
                .getAllByItemIdOrderByIdAsc(ITEM_ID);
    }

    @Test
    void testGetAllByRequestId() {
        List<Item> items = generateItems(10);
        when(itemRepository.getAllByRequestIdOrderByIdAsc(REQUEST_ID)).thenReturn(items);

        List<Item> result = subject.getAllByRequestIdOrderByIdAsc(REQUEST_ID);

        assertThat(result, is(items));
        verify(itemRepository, times(1))
                .getAllByRequestIdOrderByIdAsc(REQUEST_ID);
    }

    @Test
    void testAddWhenUserExists() {
        Item item = getDefaultItem();
        User owner = getDefaultUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.save(item)).thenReturn(item);

        Item result = subject.add(item);

        assertEquals(result, item);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testAddWhenUserNotExistsShouldThrow() {
        Item item = getDefaultItem();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subject.add(item));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testUpdateWhenNotOwnerShouldThrow() {
        Item item = getDefaultItem().toBuilder()
                .ownerId(USER_ID + 100)
                .build();
        Item updatedItem = getDefaultItem().toBuilder()
                .name("NEW")
                .description("NEW")
                .available(!item.getAvailable())
                .build();

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> subject.update(updatedItem));
        verify(itemRepository, times(1)).findById(ITEM_ID);
    }

    @Test
    void testUpdateWhenOwnerShouldUpdate() {
        Item item = getDefaultItem();
        Item updatedItem = item.toBuilder()
                .name("NEW")
                .description("NEW")
                .available(!item.getAvailable())
                .build();

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(updatedItem);

        Item result = subject.update(updatedItem);

        assertEquals(result, updatedItem);
        verify(itemRepository, times(1)).findById(ITEM_ID);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testDelete() {
        Item item = getDefaultItem();
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        subject.delete(item.getId());

        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void testSearchWhenTextNotEmpty() {
        List<Item> items = generateItems(PAGE_SIZE);
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        when(itemRepository.search("test", pageable))
                .thenReturn(new PageImpl<>(items));

        Page<Item> result = subject.search("test", 0, PAGE_SIZE);

        assertEquals(result.toList(), items);
        verify(itemRepository, times(1)).search("test", pageable);
    }

    @Test
    void testSearchWhenTextEmptyReturnEmpty() {
        Page<Item> result = subject.search("", 0, PAGE_SIZE);

        assertEquals(result.toList(), Collections.emptyList());
        verify(itemRepository, times(0)).search(any(), any());
    }

    @Test
    void testAddCommentWhenHasBooking() {
        Booking booking = getDefaultBooking();
        Comment comment = getDefaultComment();
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = subject.addComment(comment, List.of(booking));

        assertEquals(result, comment);
        verify(commentRepository, times(1)).save(comment);
    }


    @Test
    void testAddCommentWhenHasNoBookingShouldThrow() {
        Booking booking = getDefaultBooking();
        Comment comment = getDefaultComment();
        comment.setItemId(ITEM_ID + 1);

        assertThrows(ValidationException.class,
                () -> subject.addComment(comment, List.of(booking)));
    }
}
