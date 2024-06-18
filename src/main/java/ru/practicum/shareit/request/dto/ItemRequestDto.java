package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    private String description;

    private UserDTO requestor;

    private LocalDateTime created;
}
