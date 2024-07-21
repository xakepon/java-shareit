package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDTO;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService service;

    @GetMapping("/{itemId}")
    public ItemDTO getItem(@PathVariable Long itemId,
                           @RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос на Item по itemId {}", itemId);
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDTO> getAllItems(@RequestHeader(OWNER_ID) Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Сделан запрос на получение всех Айтемов: userId={}, from={}, size={}", userId, from, size);
        return service.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск по тексту: text={}, from={}, size={}", text, from, size);
        return service.search(text, from, size);
    }

    @PostMapping
    public ItemDTO createItem(@RequestBody ItemDTO itemDto,
                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на создание Item по userId {}, itemDto {}", userId, itemDto);
        return service.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(@PathVariable Long itemId,
                                    @RequestBody CommentDTO commentDto,
                                    @RequestHeader(OWNER_ID) Long userId) {
        log.info("Сделан комментарий со слежующими параметрами: itemId {}, userId {}, text {}", itemId, userId, commentDto);
        return service.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@RequestBody ItemDTO itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Сделан запрос на обновление Айтема с параметрами userId {}, itemId {}, itemDto {}", userId, itemId, itemDto);
        return service.update(itemDto, itemId, userId);
    }

}
