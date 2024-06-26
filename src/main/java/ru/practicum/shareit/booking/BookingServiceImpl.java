package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(InputBookingDTO inputBookingDto, Long bookerId) {
        if (inputBookingDto.getStart().isAfter(inputBookingDto.getEnd()) ||
                inputBookingDto.getStart().equals(inputBookingDto.getEnd())) {
            throw new ValidationException("Ошибка - неверное время бронирования!");
        }

        ItemDto itemDto = itemService.getById(inputBookingDto.getItemId());
        if (itemDto.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Ошибка - владелец не может быть бронирующим!");
        }

        UserDTO userDto = userService.get(bookerId);
        Booking booking = bookingMapper.toBooking(inputBookingDto, userDto, itemDto);
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Ошибка - item не может быть заброирован !");
        }
        repository.save(booking);

        BookingDto createdBookingDto = bookingMapper.toBookingDto(booking);
        log.info("Выполнен метод createBooking с данными" + " inputBookingDto:{}, bookerId:{} /" +
                " createdBookingDto:{}", inputBookingDto, bookerId, createdBookingDto);
        return bookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean isApproved) {
        userService.get(userId);

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Ошибка бронирование не найдено!"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Ошибка - бронирование уже выполенно!");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Ошибка - только пользователь может подтвердить бронирование!");
        }

        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        repository.save(booking);

        BookingDto approvedBookingDto = bookingMapper.toBookingDto(booking);
        log.info("выполнен метод approve с парамтерами" + " userId:{}, bookingId:{}, isApproved:{} / " +
                        " bookingStatus:{}, approvedBookingDto:{}",
                userId, bookingId, isApproved, newStatus, approvedBookingDto);
        return approvedBookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
               .orElseThrow(() -> new NotFoundException("Ошибка - номер бронирования не найден!"));

        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Ошибка userId не равен bookingId или не равен OwnerId!");
        }

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        log.info("выполнен метод getById с параметрами" + " userId:{}, bookingId:{} / bookingDto:{}",
                userId, bookingId, bookingDto);
        return bookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllUserBookings(Long userId, String state) {
        userService.get(userId);
        validateState(state);

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDto> allList = getBookingDtoList(repository.findByBookerIdOrderByStartDesc(userId));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDto> currentList = getBookingDtoList(repository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now()));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDto> pastList = getBookingDtoList(repository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now()));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDto> futureList = getBookingDtoList(repository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now()));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDto> waitingList = getBookingDtoList(repository.findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        userId, LocalDateTime.now(), BookingStatus.WAITING));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDto> rejectedList = getBookingDtoList(repository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllOwnerBookings(Long ownerId, String state) {
        userService.get(ownerId);
        validateState(state);

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDto> allList = getBookingDtoList(repository.findByItemOwnerIdOrderByStartDesc(ownerId));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDto> currentList = getBookingDtoList(repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now()));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDto> pastList = getBookingDtoList(repository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now()));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDto> futureList = getBookingDtoList(repository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now()));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDto> waitingList = getBookingDtoList(repository.findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        ownerId, LocalDateTime.now(), BookingStatus.WAITING));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDto> rejectedList = getBookingDtoList(repository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    private List<BookingDto> getBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateState(String state) {
        try {
            BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
    }
}
