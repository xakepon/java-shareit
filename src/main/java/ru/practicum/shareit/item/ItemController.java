package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос на Item по itemId {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(OWNER_ID) Long userId,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Сделан запрос на получение всех Айтемов: userId={}, from={}, size={}", userId, from, size);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestParam String text,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск по тексту: text={}, from={}, size={}", text, from, size);
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto createItem(@Validated @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на создание Item по userId {}, itemDto {}", userId, itemDto);
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(@PathVariable Long itemId,
                                    @Validated @RequestBody CommentDTO commentDto,
                                    @RequestHeader(OWNER_ID) Long userId) {
        log.info("Сделан комментарий со слежующими параметрами: itemId {}, userId {}, text {}", itemId, userId, commentDto);
        return itemService.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Сделан запрос на обновление Айтема с параметрами userId {}, itemId {}, itemDto {}", userId, itemId, itemDto);
        return itemService.update(itemDto, itemId, userId);
    }

}
