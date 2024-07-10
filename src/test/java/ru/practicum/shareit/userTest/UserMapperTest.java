package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
     UserMapper userMapper;

     static final Long USER_ID = 1L;

     final User user = new User(USER_ID, "user", "user@user.user");
     final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");

    @Test
    void toUser_successfully() {
        User actUser = userMapper.toUser(userDto);
        assertEquals(actUser.getId(), user.getId());
        assertEquals(actUser.getName(), user.getName());
        assertEquals(actUser.getEmail(), user.getEmail());
    }

    @Test
    void toUserDto_successfully() {
        UserDTO actUserDto = userMapper.toUserDto(user);
        assertEquals(actUserDto.getId(), userDto.getId());
        assertEquals(actUserDto.getName(), userDto.getName());
        assertEquals(actUserDto.getEmail(), userDto.getEmail());
    }

}
