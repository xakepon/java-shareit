package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Service
public interface ItemService {
    ItemDto getItemById(Long itemId, Long userId);

    ItemDto getById(Long itemId);

    List<ItemDto> getAll(Long ownerId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    ItemDto create(ItemDto itemDto, Long userId);

    CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDto);

    //ItemDto save(ItemDto itemDto, Long itemId, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemRequest getItemRequest(ItemDto itemDto);

}
