package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Service
public interface ItemService {

    ItemDTO getItemById(Long itemId, Long userId);

    ItemDTO getById(Long itemId);

    List<ItemDTO> getAll(Long ownerId, int from, int size);

    List<ItemDTO> search(String text, int from, int size);

    ItemDTO create(ItemDTO itemDto, Long userId);

    CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDto);

    ItemDTO update(ItemDTO itemDto, Long itemId, Long userId);

    ItemRequest getItemRequest(ItemDTO itemDto);

}
