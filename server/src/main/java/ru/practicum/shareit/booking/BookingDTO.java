package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDTO item;
    private UserDTO booker;
    private BookingStatus status;
}
