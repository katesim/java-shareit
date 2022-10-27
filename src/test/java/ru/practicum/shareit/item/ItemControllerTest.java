package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ItemMapper itemMapper = new ItemMapper();

    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final User user = new User(1L, "user1", "user1@user.ru");
    private final Item item = new Item(1L, "item1", "description1",
            true, user.getId(), 1L);
    private final ItemDto itemDto = new ItemDto(1L, "item1", "description1",
            true, 1L);

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllTest() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemService.getAllByOwnerIdOrderByIdAsc(item.getOwnerId(), 0, 10))
                .thenReturn(new PageImpl<>(items));
        when(bookingService.getNextBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);

        when(itemService.getAllCommentsByItemIdOrderByIdAsc(item.getId()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, item.getOwnerId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].available", is(true)));
        verify(itemService, times(1))
                .getAllByOwnerIdOrderByIdAsc(item.getOwnerId(), 0, 10);
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemService.getById(item.getId()))
                .thenReturn(item);
        when(bookingService.getNextBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);

        when(itemService.getAllCommentsByItemIdOrderByIdAsc(item.getId()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, item.getOwnerId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item1")))
                .andExpect(jsonPath("$.description", is("description1")))
                .andExpect(jsonPath("$.available", is(true)));
        verify(itemService, times(1))
                .getById(item.getOwnerId());
    }

//    @Test
//    void createTest() throws Exception {
//        when(itemService.add(item)).thenReturn(item);
//
//        mockMvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header(USER_ID_HEADER, 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId())))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId().intValue())));
//    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .delete(1L);
    }


}
