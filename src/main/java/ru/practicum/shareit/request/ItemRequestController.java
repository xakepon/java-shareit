package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос getOwnItemRequests: userId={}, from={}, size={}", userId, from, size);
        return service.getOwnItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size,
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
    public ItemRequestDto createItemRequest(@Validated @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос createItemRequest: userId={}, description={}", userId, itemRequestDto);
        return service.create(userId, itemRequestDto);
    }
}
