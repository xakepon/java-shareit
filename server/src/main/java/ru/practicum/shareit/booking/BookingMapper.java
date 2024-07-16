package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public BookingDTO toBookingDTO(Booking booking) {
        return booking == null ? null : BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(itemMapper.toItemDTO(booking.getItem()))
                .booker(userMapper.toUserDTO(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(InputBookingDTO inputBookingDTO, UserDTO userDTO, ItemDTO itemDTO) {
        return inputBookingDTO == null ? null : Booking.builder()
                .start(inputBookingDTO.getStart())
                .end(inputBookingDTO.getEnd())
                .item(ItemMapper.toItem(itemDTO))
                .booker(userMapper.toUser(userDTO))
                .status(BookingStatus.WAITING)
                .build();
    }

    public ShortBookingDTO toItemBookingDTO(Booking booking) {
        return booking == null ? null : ShortBookingDTO.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

}
