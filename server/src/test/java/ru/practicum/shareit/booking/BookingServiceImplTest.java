package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingTestUtils.USER_ID;
import static ru.practicum.shareit.booking.BookingTestUtils.generateBookings;
import static ru.practicum.shareit.booking.BookingTestUtils.getDefaultBooking;
import static ru.practicum.shareit.item.ItemTestUtils.generateItems;
import static ru.practicum.shareit.item.ItemTestUtils.getDefaultItem;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final int PAGE_START_FROM = 0;
    private static final int PAGE_SIZE_DEFAULT = 10;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl subject;

    @Test
    void getAll() {
        List<Booking> bookings = generateBookings(10);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = subject.getAll();

        assertEquals(bookings, result);
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserNotExistsShouldThrow() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getAllByUserIdOrderByStartDesc(
                        user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT));

        verify(userRepository, times(1))
                .findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsAll() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable))
                .thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(user.getId(), pageable);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsCurrent() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.CURRENT, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsPast() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                eq(user.getId()), any(LocalDateTime.class), eq(Status.APPROVED), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.PAST, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        eq(user.getId()), any(LocalDateTime.class), eq(Status.APPROVED), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsFuture() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                eq(user.getId()), any(LocalDateTime.class), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.FUTURE, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(
                        eq(user.getId()), any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsWaiting() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                eq(user.getId()), eq(Status.WAITING), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.WAITING, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusEqualsOrderByStartDesc(
                        eq(user.getId()), eq(Status.WAITING), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByUserIdOrderByStartDescWhenUserExistsAndStateIsRejected() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                eq(user.getId()), eq(Status.REJECTED), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByUserIdOrderByStartDesc(
                user.getId(), State.REJECTED, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusEqualsOrderByStartDesc(
                        eq(user.getId()), eq(Status.REJECTED), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserNotExistsShouldThrow() {
        User user = getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> subject.getAllByOwnerIdOrderByStartDesc(
                        user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT));

        verify(userRepository, times(1))
                .findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsAll() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInOrderByStartDesc(itemIds, pageable))
                .thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInOrderByStartDesc(itemIds, pageable);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsCurrent() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
                eq(itemIds), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.CURRENT, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
                        eq(itemIds), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsPast() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                eq(itemIds), any(LocalDateTime.class), eq(Status.APPROVED), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.PAST, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        eq(itemIds), any(LocalDateTime.class), eq(Status.APPROVED), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsFuture() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInAndStartIsAfterOrderByStartDesc(
                eq(itemIds), any(LocalDateTime.class), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.FUTURE, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInAndStartIsAfterOrderByStartDesc(
                        eq(itemIds), any(LocalDateTime.class), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsWaiting() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                eq(itemIds), eq(Status.WAITING), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.WAITING, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInAndStatusEqualsOrderByStartDesc(
                        eq(itemIds), eq(Status.WAITING), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByOwnerIdOrderByStartDescWhenUserExistsAndStateIsRejected() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        List<Item> items = generateItems(PAGE_SIZE_DEFAULT);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        User user = getDefaultUser();
        Pageable pageable = PageRequest.of(PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE))
        ).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                eq(itemIds), eq(Status.REJECTED), eq(pageable))
        ).thenReturn(new PageImpl<>(bookings));

        Page<Booking> result = subject.getAllByOwnerIdOrderByStartDesc(
                user.getId(), State.REJECTED, PAGE_START_FROM, PAGE_SIZE_DEFAULT);

        assertEquals(bookings, result.toList());
        verify(userRepository, times(1))
                .findById(user.getId());
        verify(itemRepository, times(1)).getAllByOwnerIdOrderByIdAsc(
                user.getId(), PageRequest.of(PAGE_START_FROM, Integer.MAX_VALUE));
        verify(bookingRepository, times(1))
                .findByItemIdInAndStatusEqualsOrderByStartDesc(
                        eq(itemIds), eq(Status.REJECTED), eq(pageable));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByIdWhenUserIdMatchOwnerAndBooker() {
        Booking booking = getDefaultBooking();
        Item item = getDefaultItem();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        Booking result = subject.getById(booking.getItemId(), USER_ID);

        assertEquals(booking, result);
        assertEquals(booking.getBookerId(), USER_ID);
        assertEquals(item.getOwnerId(), USER_ID);
        verify(bookingRepository, times(1))
                .findById(booking.getId());
        verify(itemRepository, times(1))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByIdWhenUserIdMismatchOwnerOrBooker() {
        Long userId = 123L;
        Booking booking = getDefaultBooking();
        Item item = getDefaultItem();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> subject.getById(booking.getItemId(), userId));
        assertNotEquals(booking.getBookerId(), userId);
        assertNotEquals(item.getOwnerId(), userId);
        verify(bookingRepository, times(1))
                .findById(booking.getId());
        verify(itemRepository, times(1))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByItemId() {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        Item item = getDefaultItem();
        when(bookingRepository.findByItemIdAndStatusEquals(item.getId(), Status.APPROVED))
                .thenReturn(bookings);

        List<Booking> result = subject.getByItemId(item.getId(), Status.APPROVED);

        assertEquals(bookings, result);
        verify(bookingRepository, times(1))
                .findByItemIdAndStatusEquals(item.getId(), Status.APPROVED);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getLastBookingByItemId() {
        Item item = getDefaultItem();
        Booking booking = getDefaultBooking();
        Booking lastBooking = getDefaultBooking();
        lastBooking.setEnd(booking.getEnd().plusDays(1));
        when(bookingRepository.findByItemIdAndStatusEquals(item.getId(), Status.APPROVED))
                .thenReturn(List.of(lastBooking, booking));

        Booking result = subject.getLastBookingByItemId(item.getId(), Status.APPROVED);

        assertEquals(lastBooking, result);
        verify(bookingRepository, times(1))
                .findByItemIdAndStatusEquals(item.getId(), Status.APPROVED);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getLastBookingByItemIdWhenEmptyBookings() {
        Item item = getDefaultItem();
        when(bookingRepository.findByItemIdAndStatusEquals(item.getId(), Status.APPROVED))
                .thenReturn(Collections.emptyList());

        Booking result = subject.getLastBookingByItemId(item.getId(), Status.APPROVED);

        assertNull(result);
        verify(bookingRepository, times(1))
                .findByItemIdAndStatusEquals(item.getId(), Status.APPROVED);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getNextBookingByItemId() {
        Item item = getDefaultItem();
        Booking booking = getDefaultBooking();
        Booking nextBooking = getDefaultBooking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(nextBooking.getStart().plusDays(1));
        when(bookingRepository.findByItemIdAndStatusEquals(item.getId(), Status.APPROVED))
                .thenReturn(List.of(nextBooking, booking));

        Booking result = subject.getNextBookingByItemId(item.getId(), Status.APPROVED);

        assertEquals(nextBooking, result);
        verify(bookingRepository, times(1))
                .findByItemIdAndStatusEquals(item.getId(), Status.APPROVED);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getNextBookingByItemIdWhenEmptyBookings() {
        Item item = getDefaultItem();
        when(bookingRepository.findByItemIdAndStatusEquals(item.getId(), Status.APPROVED))
                .thenReturn(Collections.emptyList());

        Booking result = subject.getNextBookingByItemId(item.getId(), Status.APPROVED);

        assertNull(result);
        verify(bookingRepository, times(1))
                .findByItemIdAndStatusEquals(item.getId(), Status.APPROVED);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void addWhenBookingIsValidAndUserExists() {
        LocalDateTime now = LocalDateTime.now();
        Long bookerId = 123L;
        Long ownerId = 456L;
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        User booker = getDefaultUser().toBuilder()
                .id(bookerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .status(null)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .bookerId(bookerId)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = subject.add(booking);

        assertEquals(booking, result);
        assertEquals(booking.getStatus(), Status.WAITING);
        assertTrue(item.getAvailable());
        assertNotEquals(booking.getBookerId(), item.getOwnerId());
        verify(bookingRepository, times(1))
                .save(booking);
        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(userRepository, times(1))
                .findById(booker.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addWhenBookingIsValidAndItemNotAvailableShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        Item item = getDefaultItem().toBuilder()
                .available(false)
                .build();
        Long bookerId = 123L;
        Booking booking = getDefaultBooking().toBuilder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .bookerId(bookerId)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> subject.add(booking));
        assertFalse(item.getAvailable());
        assertNotEquals(booking.getBookerId(), item.getOwnerId());

        verify(itemRepository, times(1))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addWhenBookingIsValidAndBookerIsOwnerShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        Long bookerId = 123L;
        User booker = getDefaultUser().toBuilder()
                .id(bookerId)
                .build();
        Item item = getDefaultItem().toBuilder()
                .ownerId(bookerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .bookerId(bookerId)
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> subject.add(booking));
        assertEquals(booking.getBookerId(), item.getOwnerId());

        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(userRepository, times(1))
                .findById(booker.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addWhenBookingStartDateIsInvalidShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        Long bookerId = 123L;
        Long ownerId = 456L;
        User booker = getDefaultUser().toBuilder()
                .id(bookerId)
                .build();
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .start(now.minusDays(1))
                .end(now.plusDays(2))
                .bookerId(bookerId)
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> subject.add(booking));
        assertNotEquals(booking.getBookerId(), item.getOwnerId());

        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(userRepository, times(1))
                .findById(booker.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addWhenBookingEndDateIsInvalidShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        Long bookerId = 123L;
        Long ownerId = 456L;
        User booker = getDefaultUser().toBuilder()
                .id(bookerId)
                .build();
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .bookerId(bookerId)
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> subject.add(booking));
        assertNotEquals(booking.getBookerId(), item.getOwnerId());

        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(userRepository, times(1))
                .findById(booker.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addWhenBookingEndDateBeforeStartShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        Long bookerId = 123L;
        Long ownerId = 456L;
        User booker = getDefaultUser().toBuilder()
                .id(bookerId)
                .build();
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .start(now.plusDays(2))
                .end(now.plusDays(1))
                .bookerId(bookerId)
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> subject.add(booking));
        assertNotEquals(booking.getBookerId(), item.getOwnerId());

        verify(itemRepository, times(1))
                .findById(item.getId());
        verify(userRepository, times(1))
                .findById(booker.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateStatusWhenOwnerIsApprovingAndStatusIsWaitingShouldBecomeApproved() {
        Long bookerId = 123L;
        Long ownerId = 456L;
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .status(Status.WAITING)
                .bookerId(bookerId)
                .build();
        Booking approvedBooking = booking.toBuilder()
                .status(Status.APPROVED)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = subject.updateStatus(booking.getId(), ownerId, true);

        assertEquals(approvedBooking, result);
        verify(bookingRepository, times(1))
                .save(booking);
        verify(bookingRepository, times(1))
                .findById(item.getId());
        verify(itemRepository, times(2))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateStatusWhenOwnerIsRejectingAndStatusIsWaitingShouldBecomeRejected() {
        Long bookerId = 123L;
        Long ownerId = 456L;
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .status(Status.WAITING)
                .bookerId(bookerId)
                .build();
        Booking rejectedBooking = booking.toBuilder()
                .status(Status.REJECTED)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = subject.updateStatus(booking.getId(), ownerId, false);

        assertEquals(rejectedBooking, result);
        verify(bookingRepository, times(1))
                .save(booking);
        verify(bookingRepository, times(1))
                .findById(item.getId());
        verify(itemRepository, times(2))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateStatusWhenStatusIsNotWaitingShouldThrowValidationException() {
        Long bookerId = 123L;
        Long ownerId = 456L;
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .status(Status.APPROVED)
                .bookerId(bookerId)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> subject.updateStatus(booking.getId(), ownerId, true));

        verify(bookingRepository, times(1))
                .findById(item.getId());
        verify(itemRepository, times(2))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateStatusWhenStatusIsWaitingAndNotOwnerIsUpdatingShouldThrowNotFoundException() {
        Long bookerId = 123L;
        Long ownerId = 456L;
        Item item = getDefaultItem().toBuilder()
                .ownerId(ownerId)
                .build();
        Booking booking = getDefaultBooking().toBuilder()
                .status(Status.WAITING)
                .bookerId(bookerId)
                .build();

        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> subject.updateStatus(booking.getId(), bookerId, true));

        verify(bookingRepository, times(1))
                .findById(item.getId());
        verify(itemRepository, times(2))
                .findById(item.getId());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
    }
}
