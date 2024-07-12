package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDTO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

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

    @NotBlank
    private String description;

    private UserDTO requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}
