package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ItemMapper {

    public static ItemDTO toItemDTO(Item item) {
        return item == null ? null : ItemDTO.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDTO itemDto) {
        return itemDto == null ? null : Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static void updateItemDTO(ItemDTO itemDto, Item itemToUpdate) {
        itemToUpdate.setName(Optional.ofNullable(itemDto.getName())
                .orElse(itemToUpdate.getName()));
        itemToUpdate.setDescription(Optional.ofNullable(itemDto.getDescription())
                .orElse(itemToUpdate.getDescription()));
        itemToUpdate.setAvailable(Optional.ofNullable(itemDto.getAvailable())
                .orElse(itemToUpdate.getAvailable()));
    }

}
