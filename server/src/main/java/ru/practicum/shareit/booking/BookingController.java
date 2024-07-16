package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final BookingService service;

    @GetMapping("/{bookingId}")
    public BookingDTO getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(OWNER_ID) Long userId) {
        log.info("Get-request getBooking: userId{}, bookingId{}", userId, bookingId);
        return service.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDTO> getAllUserBookings(@RequestHeader(OWNER_ID) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get-request getAllUserBookings: userId{}, state{}, from={}, size={}",
                userId, state, from, size);
        return service.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDTO> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(OWNER_ID) Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get-request getAllOwnerBookings: userId={}, state{}, from={}, size={}",
                userId, state, from, size);
        return service.getAllOwnerBookings(userId, state, from, size);
    }

    @PostMapping
    public BookingDTO createBooking(@RequestBody InputBookingDTO inputBookingDto,
                                    @RequestHeader(OWNER_ID) Long userId) {
        log.info("Post-request createBooking: userId{}, bookingDto{}", userId, inputBookingDto);
        return service.create(inputBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(OWNER_ID) Long userId) {
        log.info("Path-request approveBooking: userId{}, bookingId{}, approve{}", userId, bookingId, approved);
        return service.approve(userId, bookingId, approved);
    }

}
