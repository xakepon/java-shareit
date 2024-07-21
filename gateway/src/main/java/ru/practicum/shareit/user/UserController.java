package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Выполнен запрос на получение userId {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Выполнен запрос на получение пользовтелей");
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserDTO userDto) {
        log.info("Выпонен запрос на создание пользователя: userDto {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDTO userDto,
                                             @PathVariable long userId) {
        log.info("Выполнен запрос на обновление UserDTO по userID {}, userDto {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        log.info("Выполнен запрос на удаление пользователя userId {}", userId);
        userClient.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
