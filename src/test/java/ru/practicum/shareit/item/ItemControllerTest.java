package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final long COMMENT_ID = 1L;
    private static final int PAGE_START_FROM = 0;
    private static final int PAGE_SIZE_DEFAULT = 10;
    private static final int PAGE_SIZE_CUSTOM = 2;

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

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);

        configJsonProvider(mapper);
    }

    private void configJsonProvider(ObjectMapper objectMapper) {

        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
            private final MappingProvider mappingProvider = new JacksonMappingProvider(objectMapper);

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    private Item getDefaultItem() {
        return new Item(ITEM_ID, "item1", "description1", true, USER_ID, 1L);
    }

    private Comment getDefaultComment() {
        return new Comment(COMMENT_ID, "comment", ITEM_ID, USER_ID, null);
    }

    private User getDefaultUser() {
        return new User(USER_ID, "user1", "user1@user.ru");
    }

    private List<Item> generateItems(final int count) {
        List<Item> items = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            final Item item = new Item(i, "item" + i, "description" + i, true, USER_ID, i);
            items.add(item);
        }

        return items;
    }

    private void assertItemAtIndex(final String response, final List<Item> items, final int index) {
        assertThat(JsonPath.read(response, "$[" + index + "].id"),
                is(items.get(index).getId()));
        assertThat(JsonPath.read(response, "$[" + index + "].name"),
                is(items.get(index).getName()));
        assertThat(JsonPath.read(response, "$[" + index + "].description"),
                is(items.get(index).getDescription()));
        assertThat(JsonPath.read(response, "$[" + index + "].available"),
                is(items.get(index).getAvailable()));
    }

    @Test
    void testGetAllWithoutPagination() throws Exception {
        List<Item> items = generateItems(1);
        Item item = items.get(0);

        when(itemService.getAllByOwnerIdOrderByIdAsc(USER_ID, PAGE_START_FROM, PAGE_SIZE_DEFAULT))
                .thenReturn(new PageImpl<>(items));
        when(bookingService.getNextBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);

        when(itemService.getAllCommentsByItemIdOrderByIdAsc(item.getId()))
                .thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertItemAtIndex(response, items, 0);

        verify(itemService, times(1))
                .getAllByOwnerIdOrderByIdAsc(item.getOwnerId(), PAGE_START_FROM, PAGE_SIZE_DEFAULT);
    }

    @Test
    void testGetAllWithPagination() throws Exception {
        List<Item> items = generateItems(5);
        int numberOfChunks = (int) Math.ceil(5f / PAGE_SIZE_CUSTOM);

        when(bookingService.getNextBookingByItemId(anyLong(), eq(Status.APPROVED)))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(anyLong(), eq(Status.APPROVED)))
                .thenReturn(null);
        when(itemService.getAllCommentsByItemIdOrderByIdAsc(anyLong()))
                .thenReturn(new ArrayList<>());

        for (int i = 0; i < numberOfChunks; i++) {
            int startIndex = PAGE_START_FROM + PAGE_SIZE_CUSTOM * i;
            int endIndex = Math.min(startIndex + PAGE_SIZE_CUSTOM, items.size());

            List<Item> slice = items.subList(startIndex, endIndex);
            when(itemService.getAllByOwnerIdOrderByIdAsc(USER_ID, startIndex, PAGE_SIZE_CUSTOM))
                    .thenReturn(new PageImpl<>(slice));

            MvcResult result = mockMvc.perform(get("/items")
                            .header(USER_ID_HEADER, USER_ID)
                            .param("from", String.valueOf(startIndex))
                            .param("size", String.valueOf(PAGE_SIZE_CUSTOM)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(slice.size())))
                    .andReturn();

            String response = result.getResponse().getContentAsString();

            for (int index = 0; index < slice.size(); index++) {
                assertItemAtIndex(response, slice, index);
            }

            verify(itemService, times(1))
                .getAllByOwnerIdOrderByIdAsc(USER_ID, startIndex, PAGE_SIZE_CUSTOM);
        }
    }

    @Test
    void getByIdTest() throws Exception {
        Item item = getDefaultItem();

        when(itemService.getById(USER_ID))
                .thenReturn(item);
        when(bookingService.getNextBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(item.getId(), Status.APPROVED))
                .thenReturn(null);

        when(itemService.getAllCommentsByItemIdOrderByIdAsc(item.getId()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/" + item.getId())
                        .header(USER_ID_HEADER, item.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        verify(itemService, times(1)).getById(USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        Item item = getDefaultItem();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.add(ArgumentMatchers.any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, USER_ID)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/items/123")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .delete(123L);
    }

    @Test
    void testUpdate() throws Exception {
        Item item = getDefaultItem();
        item.setDescription("CustomDescription");
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.update(eq(item))).thenReturn(item);

        mockMvc.perform(patch("/items/" + item.getId())
                        .header(USER_ID_HEADER, USER_ID)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));

        verify(itemService, times(1))
                .update(eq(item));
    }

    @Test
    void testSearch() throws Exception {
        List<Item> items = generateItems(1);
        Item item = items.get(0);
        item.setDescription("CustomDescription");

        when(itemService.search("test", PAGE_START_FROM, PAGE_SIZE_DEFAULT))
                .thenReturn(new PageImpl<>(List.of(item)));

        MvcResult result = mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, USER_ID)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertItemAtIndex(response, items, 0);

        verify(itemService, times(1))
                .search("test", PAGE_START_FROM, PAGE_SIZE_DEFAULT);
    }

    @Test
    void testAddComment() throws Exception {
        Item item = getDefaultItem();
        User author = getDefaultUser();
        List<Booking> authorBookings = Collections.emptyList();
        Comment comment = getDefaultComment();
        CommentRequestDto commentRequestDto = new CommentRequestDto(comment.getText());

        when(userService.getById(USER_ID)).thenReturn(author);
        when(bookingService.getAllByUserIdOrderByStartDesc(USER_ID, State.PAST, 0, Integer.MAX_VALUE))
                .thenReturn(new PageImpl<>(authorBookings));
        when(itemService.addComment(any(Comment.class), eq(authorBookings))).thenReturn(comment);

        MvcResult result = mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .header(USER_ID_HEADER, USER_ID)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(JsonPath.read(response, "$.id"), is(comment.getId()));
        assertThat(JsonPath.read(response, "$.text"), is(comment.getText()));
        assertThat(JsonPath.read(response, "$.authorName"), is(author.getName()));
        assertThat(JsonPath.read(response, "$.created"), is(nullValue()));

        verify(userService, times(1)).getById(USER_ID);
        verify(bookingService, times(1))
                .getAllByUserIdOrderByStartDesc(USER_ID, State.PAST, 0, Integer.MAX_VALUE);
    }
}
