package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDTO add(UserDTO userDto);

    void remove(Long userId);

    UserDTO get(Long userId);

    List<UserDTO> getUsers();

    UserDTO update(UserDTO userDto, Long id);
}
