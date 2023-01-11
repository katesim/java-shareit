package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.user.UserTestUtils.USER_ID;
import static ru.practicum.shareit.user.UserTestUtils.generateUsers;
import static ru.practicum.shareit.user.UserTestUtils.getDefaultUser;
import static ru.practicum.shareit.utils.JsonTestUtils.configJsonProvider;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String USERS_ENDPOINT = "/users/";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        configJsonProvider(mapper);
    }

    private void assertUserAtIndex(final String response, final List<User> users, final int index) {
        assertThat(JsonPath.read(response, "$[" + index + "].id"),
                is(users.get(index).getId()));
        assertThat(JsonPath.read(response, "$[" + index + "].name"),
                is(users.get(index).getName()));
        assertThat(JsonPath.read(response, "$[" + index + "].email"),
                is(users.get(index).getEmail()));
    }

    @Test
    void testGetById() throws Exception {
        User user = getDefaultUser();
        when(userService.getById(USER_ID)).thenReturn(user);

        mockMvc.perform(get(USERS_ENDPOINT + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).getById(user.getId());
    }

    @Test
    void testGetByIdWhenNoUserExistShouldReturnNotFound() throws Exception {
        when(userService.getById(USER_ID)).thenThrow(new NotFoundException(""));

        mockMvc.perform(get(USERS_ENDPOINT + USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(USER_ID);
    }

    @Test
    void testGetAll() throws Exception {
        List<User> users = generateUsers(10);
        when(userService.getAll()).thenReturn(users);

        MvcResult result = mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertUserAtIndex(response, users, 0);

        verify(userService, times(1)).getAll();
    }

    @Test
    void testCreate() throws Exception {
        User user = getDefaultUser();
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.add(any(User.class))).thenReturn(user);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).add(ArgumentMatchers.any(User.class));
    }

    @Test
    void testUpdate() throws Exception {
        User user = getDefaultUser();
        user.setEmail("new@email.com");
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.update(eq(user.getId()), any(User.class))).thenReturn(user);

        mockMvc.perform(patch(USERS_ENDPOINT + user.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .update(eq(user.getId()), any(User.class));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(USERS_ENDPOINT + "123"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .delete(123L);
    }
}
