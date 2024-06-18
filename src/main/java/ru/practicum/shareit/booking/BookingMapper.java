package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public final class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return booking == null ? null : BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
