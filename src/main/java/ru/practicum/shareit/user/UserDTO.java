package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
