package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private UserDTO booker;

    private BookingStatus status;
}
