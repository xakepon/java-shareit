package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
     UserRepository userRepository;

    @InjectMocks
     UserServiceImpl userService;

     static final Long USER_ID = 1L;
     static final Long WRONG_ID = 5L;

    //User
     final User user = new User(USER_ID, "user", "user@user.user");
     final User wrongUser = new User(WRONG_ID, null, null);
     final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");
     final UserDTO updatedUserDto = new UserDTO(USER_ID, "updatedUser", "user@user.user");

     MockedStatic<UserMapper> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = Mockito.mockStatic(UserMapper.class);
        lenient().when(UserMapper.toUserDto(any(User.class))).thenReturn(userDto);
        lenient().when(UserMapper.toUser(any(UserDTO.class))).thenReturn(user);
        lenient().when(UserMapper.toUser(any(UserDTO.class))).thenReturn(user);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void getById_successfullyGet() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertEquals(userDto, userService.get(USER_ID));
    }

    @Test
    void getById_notFoundUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.get(WRONG_ID));
    }

    @Test
    void getAll_successfullyGetList() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        List<UserDTO> list = userService.getUsers();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(userDto, list.get(0));
    }

    @Test
    void create_successfullyCreated() {
        when(userRepository.save(any())).thenReturn(user);
        assertEquals(userService.add(userDto), userDto);
    }

    @Test
    void create_notFoundUser() {
        when(userRepository.save(any())).thenReturn(wrongUser);
        assertThrows(NotFoundException.class, () -> userService.get(WRONG_ID));
    }

    @Test
    void update_successfullyUpdated() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDTO updatedUser = userService.update(updatedUserDto, USER_ID);
        assertNotNull(updatedUser);
        assertEquals(userDto.getName(), updatedUser.getName());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        assertEquals(userDto.getId(), updatedUser.getId());

    }

    @Test
    void update_notFoundUserId() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("fail: user Not Found!"));

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.update(userDto, WRONG_ID));
        assertEquals(exception.getMessage(), "fail: user Not Found!");
    }

    @Test
    void update_alreadyExistEmailFail() {
        UserDTO failUserDto = new UserDTO(WRONG_ID, "dailUser", "user@user.user");
        when(userRepository.findById(anyLong())).thenThrow(new AlreadyExistsException("fail: email Is Already Taken!"));

        Exception exception = assertThrows(AlreadyExistsException.class,
                () -> userService.update(failUserDto, USER_ID));
        assertEquals(exception.getMessage(), "fail: email Is Already Taken!");
    }

    @Test
    void delete_successfullyDeleted() {
        userService.remove(USER_ID);
        verify(userRepository, times(1)).deleteById(USER_ID);
    }

}
