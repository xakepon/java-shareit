package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users;
    private final Map<String, User> emailsUsers;
    private Long id;

    public UserStorageImpl() {
        id = 0L;
        users = new HashMap<>();
        emailsUsers = new HashMap<>();
    }

    private Long createId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        Long id = createId();
        user.setId(id);
        users.put(id, user);
        emailsUsers.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public User remove(Long id) {
        emailsUsers.remove(users.get(id).getEmail());
        return users.remove(id);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isContains(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isContainsEmail(String email) {
        return emailsUsers.containsKey(email);
    }

    @Override
    public Optional<Long> getUserByEmail(String email) {
       return emailsUsers.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .map(User::getId)
                .findFirst();

    }
}
