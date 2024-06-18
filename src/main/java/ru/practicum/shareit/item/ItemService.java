package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long itemId, Long userId);

    ItemDto getById(Long itemId);

    List<ItemDto> getAll(Long ownerId);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto itemDto, Long userId);

    CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDto);

    ItemDto save(ItemDto itemDto, Long itemId, Long userId);

}
