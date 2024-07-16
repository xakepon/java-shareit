package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
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
    private final BookingMapper bookingMapper;

    @Override
    public BookingDTO create(InputBookingDTO inputBookingDTO, Long bookerId) {
        if (inputBookingDTO.getStart().isAfter(inputBookingDTO.getEnd()) ||
                inputBookingDTO.getStart().equals(inputBookingDTO.getEnd())) {
            throw new ValidationException("fail: invalid booking time!");
        }

        ItemDTO itemDTO = itemService.getById(inputBookingDTO.getItemId());
        if (itemDTO.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("fail: owner can not be a booker!");
        }

        if (!itemDTO.getAvailable()) {
            throw new ValidationException("fail: item cannot be booked!");
        }

        UserDTO userDTO = userService.getById(bookerId);
        Booking booking = bookingMapper.toBooking(inputBookingDTO, userDTO, itemDTO);
        Booking savedBooking = repository.save(booking);
        BookingDTO createdBookingDTO = bookingMapper.toBookingDTO(savedBooking);
        log.info("method: createBooking |Request/Response|" + " inputBookingDTO:{}, bookerId:{} /" +
                " createdBookingDTO:{}", inputBookingDTO, bookerId, createdBookingDTO);
        return bookingMapper.toBookingDTO(savedBooking);
    }

    @Override
    public BookingDTO approve(Long userId, Long bookingId, Boolean status) {
        userService.getById(userId);

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("fail: bookingId Not Found!"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("fail: booking is already approved!");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("fail: only user can approve booking!");
        }

        BookingStatus newStatus = status ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        repository.save(booking);

        BookingDTO approvedBookingDTO = bookingMapper.toBookingDTO(booking);
        log.info("method: approve |Request/Response|" + " userId:{}, bookingId:{}, isApproved:{} / " +
                        " bookingStatus:{}, approvedBookingDTO:{}",
                userId, bookingId, status, newStatus, approvedBookingDTO);
        return approvedBookingDTO;
    }

    @Override
    public BookingDTO getById(Long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("fail: bookingId Not Found!"));

        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("fail: userId not equals bookerId or equals ownerId!");
        }

        BookingDTO bookingDTO = bookingMapper.toBookingDTO(booking);
        log.info("method: getById |Request/Response|" + " userId:{}, bookingId:{} / bookingDTO:{}",
                userId, bookingId, bookingDTO);
        return bookingDTO;
    }

    @Override
    public List<BookingDTO> getAllUserBookings(Long userId, String state, int from, int size) {
        userService.getById(userId);
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDTO> allList = getBookingDTOList(repository.findByBookerIdOrderByStartDesc(userId, pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDTO> currentList = getBookingDTOList(repository.findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDTO> pastList = getBookingDTOList(repository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDTO> futureList = getBookingDTOList(repository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDTO> waitingList = getBookingDTOList(repository.findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        userId, LocalDateTime.now(), BookingStatus.WAITING, pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDTO> rejectedList = getBookingDTOList(repository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageRequest));
                log.info("method: getAllUserBookings |Request/Response|" + " userId={}, state={} / list={}",
                        userId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDTO> getAllOwnerBookings(Long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        switch (BookingState.valueOf(state)) {
            case ALL:
                List<BookingDTO> allList = getBookingDTOList(repository.findByItemOwnerIdOrderByStartDesc(ownerId, pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, allList);
                return allList;
            case CURRENT:
                List<BookingDTO> currentList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, currentList);
                return currentList;
            case PAST:
                List<BookingDTO> pastList = getBookingDTOList(repository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, pastList);
                return pastList;
            case FUTURE:
                List<BookingDTO> futureList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, futureList);
                return futureList;
            case WAITING:
                List<BookingDTO> waitingList = getBookingDTOList(repository.findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(
                        ownerId, LocalDateTime.now(), BookingStatus.WAITING, pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, waitingList);
                return waitingList;
            case REJECTED:
                List<BookingDTO> rejectedList = getBookingDTOList(repository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED, pageRequest));
                log.info("method: getAllOwnerBookings |Request/Response|" + " ownerId={}, state={} / list={}",
                        ownerId, state, rejectedList);
                return rejectedList;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
    }

    private List<BookingDTO> getBookingDTOList(Page<Booking> bookings) {
        return bookings.stream()
                .map(bookingMapper::toBookingDTO)
                .collect(Collectors.toList());
    }

}