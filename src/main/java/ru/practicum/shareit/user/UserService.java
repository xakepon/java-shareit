package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDTO add(UserDTO userDto);

    UserDTO update(UserDTO userDto, Long id);

    UserDTO remove(Long userId);

    UserDTO get(Long userId);

    List<UserDTO> getUsers();
}
