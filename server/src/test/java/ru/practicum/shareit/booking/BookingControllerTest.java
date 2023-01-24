package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingTestUtils.assertBookingAtIndex;
import static ru.practicum.shareit.booking.BookingTestUtils.generateBookings;
import static ru.practicum.shareit.booking.BookingTestUtils.getDefaultBooking;
import static ru.practicum.shareit.common.ShareItConstants.USER_ID_HEADER;
import static ru.practicum.shareit.item.ItemTestUtils.getDefaultItem;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;
import static ru.practicum.shareit.utils.JsonTestUtils.configJsonProvider;


@WebMvcTest(controllers = BookingController.class)
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private static final String BOOKINGS_ENDPOINT = "/bookings/";

    private static final int PAGE_START_FROM = 0;
    private static final int PAGE_SIZE_DEFAULT = 10;

    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;
    @SpyBean
    private BookingMapper bookingMapper;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        configJsonProvider(mapper);
    }

    @Test
    void getById() throws Exception {
        Booking booking = getDefaultBooking();
        Item item = getDefaultItem();
        User user = getDefaultUser();
        when(bookingService.getById(booking.getId(), booking.getBookerId())).thenReturn(booking);
        when(userService.getById(user.getId())).thenReturn(user);
        when(itemService.getById(item.getId())).thenReturn(item);

        mockMvc.perform(get(BOOKINGS_ENDPOINT + booking.getId())
                        .header(USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.item.description", is(item.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.booker.name", is(user.getName())));

        verify(bookingService, times(1))
                .getById(booking.getId(), booking.getBookerId());
        verify(itemService, times(1)).getById(item.getId());
        verify(userService, times(1)).getById(user.getId());
    }

    @Test
    void getAllByUserId() throws Exception {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        Item item = getDefaultItem();
        User user = getDefaultUser();
        when(bookingService.getAllByUserIdOrderByStartDesc(user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT))
                .thenReturn(new PageImpl<>(bookings));
        when(userService.getById(user.getId())).thenReturn(user);
        when(itemService.getById(item.getId())).thenReturn(item);

        MvcResult result = mockMvc.perform(get(BOOKINGS_ENDPOINT)
                        .header(USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        for (int index = 0; index < PAGE_SIZE_DEFAULT; index++) {
            assertBookingAtIndex(response, bookings, user, item, index);
        }

        verify(bookingService, times(1))
                .getAllByUserIdOrderByStartDesc(user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        verify(itemService, times(PAGE_SIZE_DEFAULT)).getById(item.getId());
        verify(userService, times(PAGE_SIZE_DEFAULT)).getById(user.getId());
    }

    @Test
    void getAllByOwnerId() throws Exception {
        List<Booking> bookings = generateBookings(PAGE_SIZE_DEFAULT);
        Item item = getDefaultItem();
        User user = getDefaultUser();
        when(bookingService.getAllByOwnerIdOrderByStartDesc(user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT))
                .thenReturn(new PageImpl<>(bookings));
        when(userService.getById(user.getId())).thenReturn(user);
        when(itemService.getById(item.getId())).thenReturn(item);

        MvcResult result = mockMvc.perform(get(BOOKINGS_ENDPOINT + "owner")
                        .header(USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        for (int index = 0; index < PAGE_SIZE_DEFAULT; index++) {
            assertBookingAtIndex(response, bookings, user, item, index);
        }

        verify(bookingService, times(1))
                .getAllByOwnerIdOrderByStartDesc(user.getId(), State.ALL, PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        verify(itemService, times(PAGE_SIZE_DEFAULT)).getById(item.getId());
        verify(userService, times(PAGE_SIZE_DEFAULT)).getById(user.getId());
    }

    @Test
    void create() throws Exception {
        Booking booking = getDefaultBooking();
        Item item = getDefaultItem();
        User user = getDefaultUser();

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .id(booking.getId())
                .start(booking.getStart().toString())
                .end(booking.getEnd().toString())
                .itemId(booking.getItemId())
                .status(booking.getStatus())
                .build();

        when(bookingService.add(any(Booking.class))).thenReturn(booking);
        when(userService.getById(user.getId())).thenReturn(user);
        when(itemService.getById(item.getId())).thenReturn(item);

        mockMvc.perform(post(BOOKINGS_ENDPOINT)
                        .header(USER_ID_HEADER, user.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.item.description", is(item.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.booker.name", is(user.getName())));

        verify(bookingService, times(1))
                .add(any(Booking.class));
        verify(itemService, times(1)).getById(item.getId());
        verify(userService, times(1)).getById(user.getId());
    }

    @Test
    void update() throws Exception {
        Booking booking = getDefaultBooking();
        Item item = getDefaultItem();
        User user = getDefaultUser();
        when(bookingService.updateStatus(booking.getId(), user.getId(), true))
                .thenReturn(booking);
        when(userService.getById(user.getId())).thenReturn(user);
        when(itemService.getById(item.getId())).thenReturn(item);

        mockMvc.perform(patch(BOOKINGS_ENDPOINT + booking.getId())
                        .header(USER_ID_HEADER, user.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.item.description", is(item.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.booker.name", is(user.getName())));

        verify(bookingService, times(1))
                .updateStatus(booking.getId(), user.getId(), true);
        verify(itemService, times(1)).getById(item.getId());
        verify(userService, times(1)).getById(user.getId());
    }
}
