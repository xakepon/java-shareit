package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;


@AllArgsConstructor
public class BookingMapper {

    public static BookingDTO toBookingDTO(Booking booking) {
        return booking == null ? null : BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(ItemMapper.toItemDTO(booking.getItem()))
                .booker(UserMapper.toUserDTO(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(InputBookingDTO inputBookingDTO, UserDTO userDTO, ItemDTO itemDTO) {
        return inputBookingDTO == null ? null : Booking.builder()
                .start(inputBookingDTO.getStart())
                .end(inputBookingDTO.getEnd())
                .item(ItemMapper.toItem(itemDTO))
                .booker(UserMapper.toUser(userDTO))
                .status(BookingStatus.WAITING)
                .build();
    }

    public static ShortBookingDTO toItemBookingDTO(Booking booking) {
        return booking == null ? null : ShortBookingDTO.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

}
