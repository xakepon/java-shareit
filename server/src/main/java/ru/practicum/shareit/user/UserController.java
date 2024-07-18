package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService service;

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        log.info("Выполнен запрос на получение userId {}", userId);
        return service.getById(userId);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Выполнен запрос на получение пользовтелей");
        return service.getAll();
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDto) {
        log.info("Выполнен запрос createItem: userDto {}", userDto);
        return service.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@RequestBody UserDTO userDto,
                              @PathVariable Long userId) {
        log.info("Выполнен запрос на обновление UserDTO по userID {}", userId, userDto);
        return service.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Выполнен запрос на создание пользователя userId {}", userId);
        service.delete(userId);
    }

}
