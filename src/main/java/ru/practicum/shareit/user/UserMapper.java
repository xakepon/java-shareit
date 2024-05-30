package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserDTO userDto) {
        return userDto == null ? null : User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserDTO toUserDto(User user) {
        return user == null ? null : UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public void updateUserDto(UserDTO userDto,User userToUpdate,Long userId) {
        userToUpdate.setEmail(userDto.getEmail() != null && !userDto.getEmail().isEmpty()
                && userDto.getEmail().contains("@") ? userDto.getEmail() : userToUpdate.getEmail());
        userToUpdate.setName(userDto.getName() != null
                && !userDto.getName().isEmpty() ? userDto.getName() : userToUpdate.getName());
    }
}
