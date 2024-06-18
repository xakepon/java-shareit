package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository repository;
    private UserMapper userMapper;

    @Override
    public UserDTO get(Long userId) {
        UserDTO getUserDto = userMapper.toUserDto(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("fail: user/owner ID Not Found!")));
        log.info("Запрос  {} с данными{}", userId, getUserDto);
        return getUserDto;
    }

    @Override
    public UserDTO add(UserDTO userDto) {
        User user = repository.save(userMapper.toUser(userDto));
        UserDTO createdUserDto = userMapper.toUserDto(user);
        log.info("Запрос на создание {} {}",
                userDto, createdUserDto);
        return createdUserDto;
    }

    @Override
    public UserDTO save(UserDTO userDto, Long userId) {
        User user = userMapper.toUser(get(userId));
        if (get(userId) == null) {
            throw new NotFoundException("Ошибка - пользователь не найден!");
        }
        Optional<User> existingUser = repository.findByIdNotAndEmail(userId, user.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("Ошибка - эл.почта уже используется!");
        }
        userMapper.updateUserDto(userDto, user);
        repository.save(user);

        UserDTO updatedUserDto = userMapper.toUserDto(user);
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
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Выполнен метод по запросу всех пользователей из списка{}", usersDto);
        return usersDto;
    }
}