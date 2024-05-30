package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistingCopyException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;
    private UserMapper userMapper;

    @Override
    public UserDTO get(Long userId) {
        UserDTO getUserDto = userMapper.toUserDto(userStorage.get(userId));
        log.info("Запрос  {} с данными{}", userId, getUserDto);
        return getUserDto;
    }

    @Override
    public UserDTO add(UserDTO userDto) {
        validateCreateUser(userDto);
        checkUserExists(userDto.getEmail());
        UserDTO createdUserDto = userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
        log.info("Запрос на создание {} {}",
                userDto, createdUserDto);
        return createdUserDto;
    }

    @Override
    public UserDTO update(UserDTO userDto, Long userId) {
        User userToUpdate = userStorage.get(userId);
        if (userToUpdate == null) {
            throw new NotFoundException("Пользовтель не найден, обновление не произведено");
        }
        userStorage.getUsers().stream()
                .filter(u -> !u.getId().equals(userId) && u.getEmail().equals(userDto.getEmail()))
                .findFirst()
                .ifPresent(user -> {
                    throw new ExistingCopyException("Электронная почта уже используется!");
                });
        userMapper.updateUserDto(userDto, userToUpdate, userId);
        UserDTO updatedUserDto = userMapper.toUserDto(userStorage.update(userToUpdate));
        return updatedUserDto;
    }

    @Override
    public UserDTO remove(Long userId) {
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь не найден, удаление не произведено");
        }
        UserDTO removeUserDto = userMapper.toUserDto(userStorage.remove(userId));
        return removeUserDto;
    }

    @Override
    public List<UserDTO> getUsers() {
        List<UserDTO> getUsersDto = userStorage.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        return getUsersDto;
    }

    private void validateCreateUser(UserDTO userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Ошибка верификации электронной почты");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Ошибка верификации имени пользователя");
        }
    }

    private void checkUserExists(String email) {
        if (userStorage.getUserByEmail(email).isPresent()) {
            throw new ExistingCopyException("Электронная почта уже используется");
        }
    }
}