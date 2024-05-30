package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;

    @Override
    public ItemDto get(Long itemId) {
        ItemDto getItemDto = itemMapper.toItemDto(itemStorage.getItemId(itemId));
        log.info("Метод get ItemDto" + "itemId:{} / itemId:{}",
                itemId, getItemDto);
        return getItemDto;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        if (userService.get(userId) == null) {
            throw new NotFoundException("Ошибка создания ItemDto т.к. имя пользователя пустое");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка создания ItemDto т.к. статус доступности нулевой");
        }
        ItemDto createdItemDto = itemMapper.toItemDto(itemStorage.create(itemMapper.toItem(itemDto, userId)));
        log.info("Создан метод ItemDto " + "itemDto:{}, userId:{} / createdItemDto:{}",
                itemDto, userId, createdItemDto);
        return createdItemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item itemToUpdate = itemStorage.getItemId(itemId);
        if (userService.get(ownerId) == null || !itemToUpdate.getOwner().equals(ownerId)) {
            throw new NotFoundException("Ошибка обновления Item не найден");
        }
        itemMapper.updateItemDto(itemDto, itemToUpdate, itemId);
        ItemDto updatedItemDto = itemMapper.toItemDto(itemStorage.update(itemMapper.toItem(itemDto, ownerId)));
        log.info("Выполнен метод обновления ItemDto " + "itemDto:{}, itemId:{}, userId:{} / createdItemDto:{}",
                itemDto, itemId, ownerId, updatedItemDto);
        return updatedItemDto;
    }

    @Override
    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemId(itemId);
        if (!item.getOwner().equals(ownerId)) {
            throw new NotFoundException("Ошибка удаления владелец не найден");
        }
        ItemDto deletedItemDto = itemMapper.toItemDto(itemStorage.delete(itemId));
        log.info("Выполнен метод на удаление ItemDto " + "itemId:{} / deletedItemDto:{}",
                itemId, ownerId);
        return deletedItemDto;
    }

    @Override
    public void deleteItemsByOwner(Long owner) {
        itemStorage.deleteItemsByOwner(owner);
        log.info("Выполнен метод по удалени по владельцу вещи" + "owner:{}",
                owner);
    }

    @Override
    public List<ItemDto> getItemByOwner(Long owner) {
        List<ItemDto> items = itemStorage.getItemByOwner(owner).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Выполнен метод удаления по владельцу вещи" + "items:{}",
                items);
        return items;
    }

    @Override
    public List<ItemDto> getItemSearch(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase();
        List<ItemDto> items = itemStorage.getItemSearch(searchText)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Выполнен метод поиска по тексту" + "search:{} / items:{}",
                text, items);
        return items;
    }
}
