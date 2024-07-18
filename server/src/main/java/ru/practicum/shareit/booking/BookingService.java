package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDTO getById(Long userId, Long bookingId);

    List<BookingDTO> getAllUserBookings(Long userId, String state, int from, int size);

    List<BookingDTO> getAllOwnerBookings(Long owner, String state, int from, int size);

    BookingDTO create(InputBookingDTO inputBookingDto, Long userId);

    BookingDTO approve(Long userId, Long bookingId, Boolean state);

}
