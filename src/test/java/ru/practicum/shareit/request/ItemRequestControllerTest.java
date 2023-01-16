package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemTestUtils.generateItems;
import static ru.practicum.shareit.request.ItemRequestTestUtils.assertItemAtIndex;
import static ru.practicum.shareit.request.ItemRequestTestUtils.assertItemRequestAtIndex;
import static ru.practicum.shareit.request.ItemRequestTestUtils.generateRequests;
import static ru.practicum.shareit.request.ItemRequestTestUtils.getDefaultRequest;
import static ru.practicum.shareit.user.UserTestUtils.USER_ID;
import static ru.practicum.shareit.utils.JsonTestUtils.configJsonProvider;

@WebMvcTest(controllers = ItemRequestController.class)
@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String REQUESTS_ENDPOINT = "/requests/";
    private static final int PAGE_START_FROM = 0;
    private static final int PAGE_SIZE_DEFAULT = 10;

    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;

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
    void create() throws Exception {
        ItemRequest itemRequest = getDefaultRequest();
        ItemRequestDescriptionDto itemRequestDto = ItemRequestDescriptionDto.builder()
                .description(itemRequest.getDescription())
                .build();

        when(itemRequestService.add(any(ItemRequest.class))).thenReturn(itemRequest);

        mockMvc.perform(post(REQUESTS_ENDPOINT)
                        .header(USER_ID_HEADER, USER_ID)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().toString())));

        verify(itemRequestService, times(1))
                .add(any(ItemRequest.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getById() throws Exception {
        List<Item> items = generateItems(10);
        ItemRequest itemRequest = getDefaultRequest();

        when(itemRequestService.getById(USER_ID, itemRequest.getId())).thenReturn(itemRequest);
        when(itemService.getAllByRequestIdOrderByIdAsc(itemRequest.getId())).thenReturn(items);

        MvcResult result = mockMvc.perform(get(REQUESTS_ENDPOINT + itemRequest.getId())
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(items.size())))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        for (int index = 0; index < items.size(); index++) {
            assertItemAtIndex(response, items, index);
        }

        verify(itemRequestService, times(1))
                .getById(USER_ID, itemRequest.getId());
        verify(itemService, times(1))
                .getAllByRequestIdOrderByIdAsc(itemRequest.getId());
        verifyNoMoreInteractions(itemRequestService);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllByRequester() throws Exception {
        List<Item> items = generateItems(10);
        List<ItemRequest> itemRequests = generateRequests(10);
        when(itemRequestService.getAllByRequesterId(USER_ID)).thenReturn(itemRequests);
        when(itemService.getAllByRequestIdOrderByIdAsc(anyLong())).thenReturn(items);

        MvcResult result = mockMvc.perform(get(REQUESTS_ENDPOINT)
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        for (int index = 0; index < itemRequests.size(); index++) {
            assertItemRequestAtIndex(response, itemRequests, items, index);
        }

        verify(itemRequestService, times(1))
                .getAllByRequesterId(USER_ID);
        verify(itemService, times(itemRequests.size()))
                .getAllByRequestIdOrderByIdAsc(anyLong());
        verifyNoMoreInteractions(itemRequestService);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllExisted() throws Exception {
        List<Item> items = generateItems(3);
        List<ItemRequest> itemRequests = generateRequests(PAGE_SIZE_DEFAULT);
        when(itemRequestService.getExistedForUserId(USER_ID, PAGE_START_FROM, PAGE_SIZE_DEFAULT))
                .thenReturn(new PageImpl<>(itemRequests));
        when(itemService.getAllByRequestIdOrderByIdAsc(anyLong())).thenReturn(items);

        MvcResult result = mockMvc.perform(get(REQUESTS_ENDPOINT + "all")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        for (int index = 0; index < itemRequests.size(); index++) {
            assertItemRequestAtIndex(response, itemRequests, items, index);
        }

        verify(itemRequestService, times(1))
                .getExistedForUserId(USER_ID, PAGE_START_FROM, PAGE_SIZE_DEFAULT);
        verify(itemService, times(itemRequests.size()))
                .getAllByRequestIdOrderByIdAsc(anyLong());
        verifyNoMoreInteractions(itemRequestService);
        verifyNoMoreInteractions(itemService);
    }
}