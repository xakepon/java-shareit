package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;
import ru.practicum.shareit.booking.dto.ShortBookingDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

@Component
@AllArgsConstructor
public final class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public BookingDto toBookingDto(Booking booking) {
        return booking == null ? null : BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(itemMapper.toItemDto(booking.getItem()))
                .booker(userMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(InputBookingDTO inputBookingDto, UserDTO userDto, ItemDto itemDto) {
        return inputBookingDto == null ? null : Booking.builder()
                .start(inputBookingDto.getStart())
                .end(inputBookingDto.getEnd())
                .item(itemMapper.toItem(itemDTO))
                .booker(userMapper.toUser(userDto))
                .status(BookingStatus.WAITING)
                .build();
    }

    public ShortBookingDTO toItemBookingDto(Booking booking) {
        return booking == null ? null : ShortBookingDTO.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
