package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    //@InjectMocks
    //private ItemMapper itemMapper;

    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long REQUEST_ID = 1L;

    private final User user = new User(USER_ID, "user", "user@user.user");
    private final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
    private final ItemDto itemDto = new ItemDto(ITEM_ID, REQUEST_ID, "item", "descriptionItem", true, null, null, user, null);

    @Test
    void toItemDto_successfully() {
        ItemDto actItemDto = ItemMapper.toItemDto(item);
        assertEquals(actItemDto.getId(), itemDto.getId());
        assertEquals(actItemDto.getName(), itemDto.getName());
        assertEquals(actItemDto.getAvailable(), itemDto.getAvailable());
        assertEquals(actItemDto.getOwner(), itemDto.getOwner());
        assertEquals(actItemDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void toItem_successfully() {
        Item actItem = ItemMapper.toItem(itemDto);
        assertEquals(actItem.getId(), item.getId());
        assertEquals(actItem.getName(), item.getName());
        assertEquals(actItem.getAvailable(), item.getAvailable());
        assertEquals(actItem.getDescription(), item.getDescription());
    }

}
