package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDTO(itemRequest.getRequestor()))
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

    private static List<ItemDTO> mapItems(ItemRequest itemRequest) {
        return itemRequest.getItems() != null ? itemRequest.getItems()
                .stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

}
