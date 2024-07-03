package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoriyTest {

    @Autowired
    private UserRepository userRepository;

    private static final Long USER_ID = 1L;
    private static final Long USER_ID_2 = 2L;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID, "user", "user@user.user");
        user2 = new User(USER_ID_2, "user2", "user2@user2.user");
    }

    @Test
    void findByIdNotAndEmail_returnEmpty() {
        Optional<User> findUser = userRepository.findByIdNotAndEmail(user2.getId(), user.getEmail());
        assertTrue(findUser.isEmpty());
    }

    @Test
    void findByIdNotAndEmail_returnUser() {
        Optional<User> findUser = userRepository.findByIdNotAndEmail(user.getId(), user.getEmail());
        assertEquals(1, List.of(findUser).size());
    }

}
