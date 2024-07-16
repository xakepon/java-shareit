package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setItems(createItemList(itemRequestDto.getItems()));
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        ItemRequestDto createdItemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        log.info("method: create |Request/Response|" + " userId={}, itemRequestDto={} / createdItemRequestDto={}",
                userId, itemRequestDto, createdItemRequestDto);
        return createdItemRequestDto;
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("fail: requestId Not Found!"));

        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(createItemDtoList(items));
        log.info("method: getById |Request/Response|" + " userId={}, requestId={} / itemRequestDto={}",
                userId, itemRequestDto, itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId, int from, int size) {
        userService.getById(userId);
        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"created"));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId, pageRequest);

        List<ItemRequestDto> itemRequestDtoList = createItemRequestList(itemRequestList);
        log.info("method: getAllItemRequests |Request/Response|" + " userId={}, page={}, size={} " +
                "/ itemRequestDtoList={}", userId, page, size, itemRequestDtoList);
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        userService.getById(userId);
        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"created"));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdIsNot(userId, pageRequest);

        List<ItemRequestDto> itemRequestDtoList = createItemRequestList(itemRequestList);
        log.info("method: getAllItemRequests |Request/Response|" + " userId={}, page={}, size={} " +
                "/ itemRequestDtoList={}", userId, page, size, itemRequestDtoList);
        return itemRequestDtoList;
    }

    private List<ItemDTO> createItemDtoList(List<Item> items) {
        return Optional.ofNullable(items)
                .map(item -> item.stream().map(itemMapper::toItemDTO).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    private List<Item> createItemList(List<ItemDTO> items) {
        return Optional.ofNullable(items)
                .map(itemDto -> itemDto.stream().map(ItemMapper::toItem).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    private List<ItemRequestDto> createItemRequestList(List<ItemRequest> requestList) {
        return requestList.stream()
                .peek(itemRequest -> itemRequest.setItems(itemRepository.findAllByItemRequest(itemRequest)))
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

}