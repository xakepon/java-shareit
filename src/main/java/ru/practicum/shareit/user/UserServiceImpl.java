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

    /*@Override
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
        /* userStorage.getUserByEmail(userDto.getEmail())
                .filter(user -> !(Objects.equals(userDto.getId(), user)))
                .ifPresent(user -> {
                            throw new ExistingCopyException("Электронная почта уже используется!");
                        });*/

        /*userMapper.updateUserDto(userDto, userToUpdate, userId);
        UserDTO updatedUserDto = userMapper.toUserDto(userStorage.update(userToUpdate));
        return updatedUserDto;
    }*/

    @Override
    public UserDTO save(UserDTO userDto, Long userId) {
        User user = userMapper.toUser(get(userId));
        if (get(userId) == null) {
            throw new NotFoundException("fail: user Not Found!");
        }
        Optional<User> existingUser = repository.findByIdNotAndEmail(userId, user.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("fail: email Is Already Taken!");
        }
        userMapper.updateUserDto(userDto, user);
        repository.save(user);

        UserDTO updatedUserDto = userMapper.toUserDto(user);
        log.info("method: save |Request/Response|" + " userDto:{}, userId:{} / updatedUserDto:{}",
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