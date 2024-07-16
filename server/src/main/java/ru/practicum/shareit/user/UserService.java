package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDTO getById(Long userId);

    List<UserDTO> getAll();

    UserDTO create(UserDTO userDto);

    UserDTO update(UserDTO userDto, Long id);

    void delete(Long userId);

}