package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User update(User user);

    User get(Long id);

    User remove(Long id);

    List<User> getUsers();

    boolean isContains(Long user);

    boolean isContainsEmail(String email);

    Optional<Long> getUserByEmail(String email);

}
