package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@AllArgsConstructor
public final class ItemRequestMapper {
    private UserService userService;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Long userId) {
        return itemRequest == null ? null : ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(userService.get(userId))
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return itemRequestDto == null ? null : ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(UserMapper.toUser(itemRequestDto.getRequestor()))
                .created(itemRequestDto.getCreated())
                .build();
    }
}
