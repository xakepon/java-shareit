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
    public UserDTO getById(Long userId) {
        UserDTO userDto = userMapper.toUserDTO(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("fail: user/owner ID Not Found!")));
        log.info("method: getById |Request/Response|" + " userId:{} / userId:{}", userId, userDto);
        return userDto;
    }

    @Override
    public UserDTO create(UserDTO userDto) {
        User user = repository.save(userMapper.toUser(userDto));
        UserDTO createdUserDto = userMapper.toUserDTO(user);
        log.info("method: create |Request/Response|" + " userDto:{} / createdUser:{}", userDto, createdUserDto);
        return createdUserDto;
    }

    @Override
    public UserDTO update(UserDTO userDto, Long userId) {
        User user = userMapper.toUser(getById(userId));
        Optional<User> existingUser = repository.findByIdNotAndEmail(userId, user.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistsException("fail: email Is Already Taken!");
        }
        userMapper.updateUserDTO(userDto, user);
        repository.save(user);

        UserDTO updatedUserDto = userMapper.toUserDTO(user);
        log.info("method: save |Request/Response|" + " userDto:{}, userId:{} / updatedUserDto:{}",
                userDto, userId, updatedUserDto);
        return updatedUserDto;
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
        log.info("method: delete |Request|" + " userId:{}", userId);
    }

    @Override
    public List<UserDTO> getAll() {
        List<UserDTO> usersDto = repository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
        log.info("method: getAll |Response|" + " list of users:{}", usersDto);
        return usersDto;
    }

}