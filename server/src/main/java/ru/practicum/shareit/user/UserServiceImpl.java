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
    public UserDTO getById(Long userId) {
        UserDTO userDto = UserMapper.toUserDTO(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("fail: user/owner ID Not Found!")));
        log.info("Запрос с данными" + " userId:{} / userId:{}", userId, userDto);
        return userDto;
    }

    @Override
    public UserDTO create(UserDTO userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        UserDTO createdUserDto = UserMapper.toUserDTO(user);
        log.info("Запрос на создание" + " userDto:{} / createdUser:{}", userDto, createdUserDto);
        return createdUserDto;
    }

    @Override
    public UserDTO update(UserDTO userDto, Long userId) {
        User user = UserMapper.toUser(getById(userId));
        Optional<User> existingUser = repository.findByIdNotAndEmail(userId, user.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("Ошибка - эл.почта уже используется");
        }
        UserMapper.updateUserDTO(userDto, user);
        repository.save(user);

        UserDTO updatedUserDto = UserMapper.toUserDTO(user);
        log.info("выполнен метод save с параметрами" + " userDto:{}, userId:{} / updatedUserDto:{}",
                userDto, userId, updatedUserDto);
        return updatedUserDto;
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
        log.info("выполнен метод по удалению пользователя" + " userId:{}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        List<UserDTO> usersDto = repository.findAll().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
        log.info("Выполнен метод по запросу всех пользователей" + "  из списка:{}", usersDto);
        return usersDto;
    }

}