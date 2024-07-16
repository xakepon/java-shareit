package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос на Item по itemId {}", itemId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(OWNER_ID) Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Сделан запрос на получение всех Айтемов: userId={}, from={}, size={}", userId, from, size);
        return itemClient.getByUser(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(OWNER_ID) Long userId,
                                             @Validated @RequestBody ItemDTO itemDto) {
        log.info("Запрос на создание Item по: userId {}, itemDto {}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(OWNER_ID) Long userId,
                                             @RequestBody ItemDTO itemDto,
                                             @PathVariable Long itemId) {
        log.info("Сделан запрос на обновление Айтема с параметрами: userId {}, itemId {}, itemDto {}", userId, itemId, itemDto);
        return itemClient.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(OWNER_ID) Long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск по тексту: userId {}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(OWNER_ID) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentDTO commentDto) {
        log.info("Сделан комментарий со следующими параметрами:  itemId {}, userId {}, text {}", itemId, userId, commentDto);
        return itemClient.createComment(itemId, userId, commentDto);
    }

}
