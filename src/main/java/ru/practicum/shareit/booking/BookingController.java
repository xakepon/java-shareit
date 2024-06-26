package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final BookingService service;


    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на бронирование userId{}, bookingId{}", userId, bookingId);
        return service.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос всех бронирований пользователелей: userId{}, state{}", userId, state);
        return service.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(OWNER_ID) Long owner) {
        log.info("ПОлучить все бронирования владельцев: ownerId{}, state{}", owner, state);
        return service.getAllOwnerBookings(owner, state);
    }

    @PostMapping
    public BookingDto createBooking(@Validated @RequestBody InputBookingDTO inputBookingDto,
                                    @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на создание бронирования: userId{}, bookingDto{}", userId, inputBookingDto);
        return service.create(inputBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(OWNER_ID) Long userId) {
        log.info("Запрос на подтверждение бронирования: userId{}, bookingId{}, approve{}", userId, bookingId, approved);
        return service.approve(userId, bookingId, approved);
    }
}
