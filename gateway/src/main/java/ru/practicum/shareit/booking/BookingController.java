package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidStateException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

        private static final String OWNER_ID = "X-Sharer-User-Id";
        private final BookingClient bookingClient;

        @GetMapping("/{bookingId}")
        public ResponseEntity<Object> getBooking(@RequestHeader(OWNER_ID) Long userId,
                                                 @PathVariable Long bookingId) {
            log.info("Запрос на бронирование bookingId={}, userId={}", bookingId, userId);
            return bookingClient.getById(userId, bookingId);
        }

        @GetMapping("/owner")
        public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(OWNER_ID) Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
            BookingState stateParam = BookingState.from(state)
                    .orElseThrow(() -> new InvalidStateException("Unknown state: " + state));
            log.info("Получить все бронирования владельцев: userId={}, state {}, from={}, size={}", userId, state, from, size);
            return bookingClient.getByOwner(userId, stateParam, from, size);
        }

        @GetMapping
        public ResponseEntity<Object> getBookingsByUser(@RequestHeader(OWNER_ID) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String state,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
            BookingState stateParam = BookingState.from(state)
                    .orElseThrow(() -> new InvalidStateException("Unknown state: " + state));
            log.info("Запрос всех бронирований пользователелей: userId={}, state {}, from={}, size={}", userId, stateParam, from, size);
            return bookingClient.getByUser(userId, stateParam, from, size);
        }

        @PostMapping
        public ResponseEntity<Object> createBooking(@RequestHeader(OWNER_ID) Long userId,
                                                    @Validated @RequestBody BookItemRequestDto requestDto) {
            log.info("Запрос на создание бронирования: userId={}, requestDto={}",userId, requestDto);
            return bookingClient.create(userId, requestDto);
        }

        @PatchMapping("/{bookingId}")
        public ResponseEntity<Object> approveBooking(@RequestHeader(OWNER_ID) Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam @NotNull Boolean approved) {
            log.info("Запрос на подтверждение бронирования: bookingId={}, userId={}", bookingId, userId);
            return bookingClient.approve(bookingId, userId, approved);
        }

    }
