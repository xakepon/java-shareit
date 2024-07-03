package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public final class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .items(mapItems(itemRequest))
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

    private List<ItemDto> mapItems(ItemRequest itemRequest) {
        return itemRequest.getItems() != null ? itemRequest.getItems()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
