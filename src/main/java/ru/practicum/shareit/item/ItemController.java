package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Получен запрос на Item по itemId {}", itemId);
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemForUser(@RequestHeader(OWNER_ID) Long userId) {
        log.info("Получен запрос на Item у userId {}", userId);
       return itemService.getItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestParam String text) {
        log.info("Получен запрос на поиск по тексту Item {}", text);
        return itemService.getItemSearch(text);
    }

    @PostMapping
    public ItemDto createItem(@Validated @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на создание Item по userId {}, itemDto {}", userId, itemDto);
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId, @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на удаление Item userId {}, itemId {}", userId, itemId);
        return itemService.delete(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на обновление Item userId {}, itemId {}, itemDto {}", userId, itemId, itemDto);
        return itemService.update(itemDto, itemId, userId);
    }
}
