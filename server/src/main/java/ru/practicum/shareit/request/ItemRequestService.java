package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(Long requestId, Long userId);

    List<ItemRequestDto> getOwnItemRequests(Long userId, int from, int size);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

}
