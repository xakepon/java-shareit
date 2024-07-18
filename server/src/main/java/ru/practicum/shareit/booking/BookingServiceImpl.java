package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDTO create(InputBookingDTO inputBookingDTO, Long bookerId) {
        if (inputBookingDTO.getStart().isAfter(inputBookingDTO.getEnd()) ||
                inputBookingDTO.getStart().equals(inputBookingDTO.getEnd())) {
            throw new ValidationException("Ошибка - неверное время бронирования!");
        }

        ItemDTO itemDTO = itemService.getById(inputBookingDTO.getItemId());
        if (itemDTO.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Ошибка - владелец не может быть бронирующим!");
        }

        if (!itemDTO.getAvailable()) {
            throw new ValidationException("Ошибка - item не может быть забронирован!");
        }

        UserDTO userDTO = userService.getById(bookerId);
        Booking booking = BookingMapper.toBooking(inputBookingDTO, userDTO, itemDTO);
        Booking savedBooking = repository.save(booking);
        BookingDTO createdBookingDTO = BookingMapper.toBookingDTO(savedBooking);
        log.info("Выполнен метод createBooking с данными" + " inputBookingDTO:{}, bookerId:{} /" +
                " createdBookingDTO:{}", inputBookingDTO, bookerId, createdBookingDTO);
        return BookingMapper.toBookingDTO(savedBooking);
    }

    @Override
    public BookingDTO approve(Long userId, Long bookingId, Boolean status) {
        userService.getById(userId);

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Ошибка бронирование не найдено!"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Ошибка - бронирование уже выполенно!");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Ошибка - только пользователь может подтвердить бронирование!");
        }

        BookingStatus newStatus = status ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        repository.save(booking);

        BookingDTO approvedBookingDTO = BookingMapper.toBookingDTO(booking);
        log.info("выполнен метод approve с параметрами" + " userId:{}, bookingId:{}, isApproved:{} / " +
                        " bookingStatus:{}, approvedBookingDTO:{}",
                userId, bookingId, status, newStatus, approvedBookingDTO);
        return approvedBookingDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getById(Long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Ошибка - номер бронирования не найден!"));

        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Ошибка userId не равен bookingId или не равен OwnerId!");
        }

        BookingDTO bookingDTO = BookingMapper.toBookingDTO(booking);
        log.info("выполнен метод getById с параметрами" + " userId:{}, bookingId:{} / bookingDTO:{}",
                userId, bookingId, bookingDTO);
        return bookingDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllUserBookings(Long userId, String state, int from, int size) {
        userService.getById(userId);
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDTO> allList = getBookingDTOList(repository.findByBookerIdOrderByStartDesc(userId, pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDTO> currentList = getBookingDTOList(repository.findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDTO> pastList = getBookingDTOList(repository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами\"" + " userId={}, state={} / list={}",
                        userId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDTO> futureList = getBookingDTOList(repository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDTO> waitingList = getBookingDTOList(repository.findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        userId, LocalDateTime.now(), BookingStatus.WAITING, pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDTO> rejectedList = getBookingDTOList(repository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageRequest));
                log.info("выполнен метод getAllUserBookings с параметрами" + " userId={}, state={} / list={}",
                        userId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllOwnerBookings(Long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDTO> allList = getBookingDTOList(repository.findByItemOwnerIdOrderByStartDesc(ownerId, pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDTO> currentList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDTO> pastList = getBookingDTOList(repository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDTO> futureList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDTO> waitingList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        ownerId, LocalDateTime.now(), BookingStatus.WAITING, pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDTO> rejectedList = getBookingDTOList(repository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED, pageRequest));
                log.info("выполнен метод getAllOwnerBookings с параметрами" + " ownerId={}, state={} / list={}",
                        ownerId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    private List<BookingDTO> getBookingDTOList(Page<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDTO)
                .collect(Collectors.toList());
    }

}