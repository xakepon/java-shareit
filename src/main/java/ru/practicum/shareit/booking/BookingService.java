package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;

import java.util.List;

public interface BookingService {
    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllUserBookings(Long userId, String state, int from, int size);

    List<BookingDto> getAllOwnerBookings(Long owner, String state, int from, int size);

    BookingDto create(InputBookingDTO inputBookingDto, Long userId);

    BookingDto approve(Long userId, Long bookingId, Boolean state);

}
