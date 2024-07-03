package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDTO get(Long userId) {
        UserDTO getUserDto = UserMapper.toUserDto(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("fail: user/owner ID Not Found!")));
        log.info("Запрос  {} с данными{}", userId, getUserDto);
        return getUserDto;
    }

    @Override
    public UserDTO add(UserDTO userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        UserDTO createdUserDto = UserMapper.toUserDto(user);
        log.info("Запрос на создание {} {}",
                userDto, createdUserDto);
        return createdUserDto;
    }

    @Override
    public UserDTO update(UserDTO userDto, Long userId) {
        User user = UserMapper.toUser(get(userId));
        Optional<User> existingUser = repository.findByIdNotAndEmail(userId, user.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("Ошибка - эл.почта уже используется!");
        }
        UserMapper.updateUserDto(userDto, user);
        repository.save(user);

        UserDTO updatedUserDto = UserMapper.toUserDto(user);
        log.info("выполнен метод save с параметрами" + " userDto:{}, userId:{} / updatedUserDto:{}",
                userDto, userId, updatedUserDto);
        return updatedUserDto;
    }

    @Override
    public void remove(Long userId) {
        repository.deleteById(userId);
        log.info("выполнен метод по удалению пользователя {}", userId);
    }

    @Override
    public List<UserDTO> getUsers() {
        List<UserDTO> usersDto = repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Выполнен метод по запросу всех пользователей из списка{}", usersDto);
        return usersDto;
    }
}