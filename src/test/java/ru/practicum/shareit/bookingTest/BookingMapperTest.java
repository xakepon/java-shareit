package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;
import ru.practicum.shareit.booking.dto.ShortBookingDTO;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    MockedStatic<UserMapper> userMapper;

    MockedStatic<ItemMapper> itemMapper;

    MockedStatic<BookingMapper> bookingMapper;

    static final Long ITEM_ID = 1L;
    static final Long USER_ID = 1L;
    static final Long BOOKING_ID = 1L;
    static final Long REQUEST_ID = 1L;

    //Users
    final User user = new User(USER_ID, "user", "user@user.user");
    final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");
    //Items
    final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
    final ItemDto itemDto = new ItemDto(ITEM_ID, REQUEST_ID, "item", "descriptionItem", true, null, null, user, null);

    final Booking booking = Booking.builder()
            .id(BOOKING_ID)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(user)
            .status(BookingStatus.WAITING)
            .build();
    final BookingDto bookingDto = BookingDto.builder()
            .id(BOOKING_ID)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(itemDto)
            .booker(userDto)
            .status(BookingStatus.WAITING)
            .build();
    final InputBookingDTO inputBookingDto = InputBookingDTO.builder()
            .itemId(ITEM_ID)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .build();


    @Test
    void toBookingDto_successfully() {
        BookingDto actBookingDto = BookingMapper.toBookingDto(booking);
        assertEquals(actBookingDto.getId(), bookingDto.getId());
        assertEquals(actBookingDto.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBooking_successfully() {
        Booking actBooking = BookingMapper.toBooking(inputBookingDto, userDto, itemDto);
        assertNotNull(actBooking);
        assertEquals(actBooking.getStart(), inputBookingDto.getStart());
        assertEquals(actBooking.getEnd(), inputBookingDto.getEnd());
    }

    @Test
    void toItemBookingDto_successfully() {
        ShortBookingDTO actShortBooking = BookingMapper.toItemBookingDto(booking);
        assertEquals(actShortBooking.getId(), bookingDto.getId());
    }

}
