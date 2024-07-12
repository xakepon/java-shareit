package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;
import ru.practicum.shareit.booking.dto.ShortBookingDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

@AllArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return booking == null ? null : BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(InputBookingDTO inputBookingDto, UserDTO userDto, ItemDto itemDto) {
        return inputBookingDto == null ? null : Booking.builder()
                .start(inputBookingDto.getStart())
                .end(inputBookingDto.getEnd())
                .item(ItemMapper.toItem(itemDto))
                .booker(UserMapper.toUser(userDto))
                .status(BookingStatus.WAITING)
                .build();
    }

    public static ShortBookingDTO toItemBookingDto(Booking booking) {
        return booking == null ? null : ShortBookingDTO.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
