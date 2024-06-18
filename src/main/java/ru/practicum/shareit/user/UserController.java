package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        log.info("Выполнен запрос на получение userId {}", userId);
        return userService.get(userId);
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        log.info("Выполнен запрос на получение пользовтелей");
        return userService.getUsers();
    }

    @ResponseBody
    @PostMapping
    public UserDTO addItem(@Validated @RequestBody UserDTO userDTO) {
        log.info("Выполнен запрос на создание Item userDto {}", userDTO);
        return userService.add(userDTO);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDTO update(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        log.info("Выполнен запрос на обновление UserDTO по userID {}, userDto {}", userId, userDTO);
        return userService.update(userDTO, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDTO remove(@PathVariable Long userId) {
        log.info("Выполнен запрос на создание пользователя userId {}", userId);
        return userService.remove(userId);
    }
}
