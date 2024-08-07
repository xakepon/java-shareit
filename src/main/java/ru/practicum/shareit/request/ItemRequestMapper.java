package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .items(mapItems(itemRequest))
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return itemRequestDto == null ? null : ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(UserMapper.toUser(itemRequestDto.getRequestor()))
                .created(itemRequestDto.getCreated())
                .build();
    }

    private static List<ItemDto> mapItems(ItemRequest itemRequest) {
        return itemRequest.getItems() != null ? itemRequest.getItems()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
