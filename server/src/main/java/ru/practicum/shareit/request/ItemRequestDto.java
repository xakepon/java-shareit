package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<ItemDTO> items;

}