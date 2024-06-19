package ru.practicum.shareit.user;

import java.util.Optional;

public final class UserMapper {
    public static User toUser(UserDTO userDto) {
        return userDto == null ? null : User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDTO toUserDto(User user) {
        return user == null ? null : UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static void updateUserDto(UserDTO userDto, User userToUpdate) {
        String updatedEmail = Optional.ofNullable(userDto.getEmail())
                .filter(email -> email.contains("@"))
                .orElse(userToUpdate.getEmail());
        userToUpdate.setEmail(updatedEmail);

        String updatedName = Optional.ofNullable(userDto.getName())
                .filter(name -> !name.isEmpty())
                .orElse(userToUpdate.getName());
        userToUpdate.setName(updatedName);
    }
}
