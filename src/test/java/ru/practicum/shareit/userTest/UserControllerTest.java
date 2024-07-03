package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Mock
    private UserRepository userRepository;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final Long USER_ID = 1L;
    private static final Long WRONG_ID = 10L;

    private final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");
    private final UserDTO wrongUserDto = new UserDTO(USER_ID, "", "user@user.user");
    private final UserDTO updatedUserDto = new UserDTO(USER_ID, "updatedUser", "updatedUser@user.user");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void createUser_successfullyCreated() throws Exception {
        when(userService.add(any(UserDTO.class))).thenReturn(userDto);
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        ArgumentCaptor<UserDTO> userDtoCaptor = ArgumentCaptor.forClass(UserDTO.class);
        verify(userService).add(userDtoCaptor.capture());

        UserDTO capturedUserDto = userDtoCaptor.getValue();
        assertThat(userDto.getId(), equalTo(capturedUserDto.getId()));
        assertThat(userDto.getName(), equalTo(capturedUserDto.getName()));
        assertThat(userDto.getEmail(), equalTo(capturedUserDto.getEmail()));
    }

    @Test
    void createUser_validationCreationFail() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    void updateUser_successfullyUpdated() throws Exception {
        when(userService.update(any(UserDTO.class), anyLong())).thenReturn(updatedUserDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updatedUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));

        ArgumentCaptor<UserDTO> userDtoCaptor = ArgumentCaptor.forClass(UserDTO.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userService).update(userDtoCaptor.capture(), userIdCaptor.capture());

        UserDTO capturedUserDto = userDtoCaptor.getValue();
        assertThat(updatedUserDto.getId(), equalTo(capturedUserDto.getId()));
        assertThat(updatedUserDto.getName(), equalTo(capturedUserDto.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(capturedUserDto.getEmail()));
    }

    @Test
    void updateUser_alreadyExistEmail() throws Exception {
        UserDTO userDtoWithExistingEmail = new UserDTO(USER_ID, "user2", "user@user.user");
        when(userService.get(anyLong())).thenReturn(userDto);
        when(userRepository.findByIdNotAndEmail(anyLong(), anyString())).thenReturn(Optional.of(new User()));
        when(userService.update(any(UserDTO.class), anyLong()))
                .thenThrow(new AlreadyExistsException("fail: email Is Already Taken!"));
        mvc.perform(patch("/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoWithExistingEmail))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userService).update(refEq(userDtoWithExistingEmail), eq(USER_ID));
    }

    @Test
    void getUser_successfullyGet() throws Exception {
        when(userService.get(eq(1L))).thenReturn(userDto);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService).get(eq(1L));
    }

    @Test
    void getUser_notFoundUserId() throws Exception {
        Mockito.when(userService.get(WRONG_ID))
                .thenThrow(new NotFoundException("fail: user/owner ID Not Found!"));

        mvc.perform(get("/users/{id}", WRONG_ID))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllUsers_successfullyGetList() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(userDto));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
        verify(userService).getUsers();
    }

    @Test
    void deleteUser_successfullyDeleted() throws Exception {
        doNothing().when(userService).remove(eq(1L));
        mvc.perform(delete("/users/1")).andExpect(status().isOk());
        verify(userService).remove(eq(1L));
    }

}
