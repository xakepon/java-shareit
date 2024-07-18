package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос getOwnItemRequests: userId={}, from={}, size={}", userId, from, size);
        return service.getOwnItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос getAllItemRequests: userId={}, from={}, size={}", userId, from, size);
        return service.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос getItemRequest: requestId={}, userId={}", requestId, userId);
        return service.getById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос createItemRequest: userId={}, description={}", userId, itemRequestDto);
        return service.create(userId, itemRequestDto);
    }

}
