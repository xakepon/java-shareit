package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDTO;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    //@Mock
    //private BookingMapper bookingMapper;
    //private MockedStatic<BookingMapper> bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

   // @Mock
   // private ItemMapper itemMapper;
    //@Mock
    //private UserMapper userMapper;
   // private MockedStatic<ItemMapper> itemMapper;
    //private MockedStatic<UserMapper> userMapper;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private static final Long BOOKING_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long OWNER_ID = 2L;
    private static final Long BOOKER_ID = 3L;
    private static final Long REQUEST_ID = 1L;
    private static final Long WRONG_ID = 10L;

    //Users
    private final User user = new User(USER_ID, "user", "user@user.user");
    private final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");
    private final User booker = new User(BOOKER_ID, "booker", "booker@booker.booker");
    private final UserDTO bookerDto = new UserDTO(BOOKER_ID, "booker", "booker@booker.booker");
    //Items
    private final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
    private final ItemDto itemDto = new ItemDto(ITEM_ID, REQUEST_ID, "item", "descriptionItem", true, null, null, user, null);
    private final InputBookingDTO inputBookingDto = new InputBookingDTO(ITEM_ID, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    //Bookings
    private final Booking booking = new Booking(BOOKING_ID, inputBookingDto.getStart(), inputBookingDto.getEnd(), item, booker, BookingStatus.WAITING);
    private final BookingDto bookingDto = new BookingDto(BOOKING_ID, ITEM_ID, booking.getStart(), booking.getEnd(), itemDto, bookerDto, BookingStatus.APPROVED);
    private final List<Booking> bookings = List.of(booking);
    private final List<BookingDto> bookingDtoList = List.of(bookingDto);
    //For ParameterizedTest
    private final Item itemForParamTest = new Item(ITEM_ID, "item", "descriptionItem", true, null, booker, null, null, null);
    private final Booking bookingForParamTest = new Booking(BOOKING_ID, inputBookingDto.getStart(), inputBookingDto.getEnd(), item, booker, BookingStatus.WAITING);

    @BeforeEach
    void setUp() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    }

    @Test
    void getById_successfullyGet() {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertEquals(bookingDto, bookingService.getById(user.getId(), booking.getId()));
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getById_notFoundBookingId() {
        when(bookingRepository.findById(WRONG_ID)).thenThrow(new NotFoundException("fail: bookingId Not Found!"));
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.getById(USER_ID, WRONG_ID));
        assertEquals(exception.getMessage(), "fail: bookingId Not Found!");
    }

    @Test
    void getById_notFoundUserIsNotOwner() {
        when(bookingRepository.findById(BOOKING_ID)).thenThrow(new NotFoundException("fail: userId not equals bookerId or equals ownerId!"));
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.getById(BOOKER_ID, BOOKING_ID));
        assertEquals(exception.getMessage(), "fail: userId not equals bookerId or equals ownerId!");

    }

    @Test
    void getUserBookings_successfullyGetList() {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        Page<Booking> bookingPage = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
        when(bookingRepository.findByBookerIdOrderByStartDesc(eq(USER_ID), eq(PageRequest.of(0, 10)))).thenReturn(bookingPage);
        List<BookingDto> allUserBookings = bookingService.getAllUserBookings(USER_ID, "ALL", 0, 10);
        assertEquals(bookingDtoList, allUserBookings);
    }

    @Test
    void getAllOwnerBookings_successfullyGetList() {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        Page<Booking> bookingPage = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(OWNER_ID, PageRequest.of(0, 10))).thenReturn(bookingPage);
        List<BookingDto> allOwnerBookings = bookingService.getAllOwnerBookings(OWNER_ID, "ALL", 0, 10);
        assertEquals(bookingDtoList, allOwnerBookings);
    }

    @Test
    void create_successfully() {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        when(BookingMapper.toBooking(any(), any(), any())).thenReturn(booking);
        when(itemService.getById(ITEM_ID)).thenReturn(itemDto);
        when(userService.get(BOOKER_ID)).thenReturn(userDto);
        BookingDto createdBookingDto = bookingService.create(inputBookingDto, BOOKER_ID);
        assertEquals(bookingDto, createdBookingDto);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_validationFail() {
        InputBookingDTO bookingBadTime = InputBookingDTO.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().minusHours(1L))
                .itemId(1L)
                .build();
        Exception exception = assertThrows(ValidationException.class, () -> bookingService.create(bookingBadTime, BOOKER_ID));
        assertEquals(exception.getMessage(), "fail: invalid booking time!");
    }

    @Test
    void create_notFoundOwnerNotBooker() {
        when(itemService.getById(any())).thenReturn(itemDto);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.create(inputBookingDto, USER_ID), "fail: owner can not be a booker!");
        assertEquals("fail: owner can not be a booker!", exception.getMessage());
    }

    @Test
    void create_validationNotAvailable() {
        itemDto.setAvailable(false);
        when(userService.get(anyLong())).thenReturn(userDto);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);
        when(itemService.getById(anyLong())).thenReturn(itemDto);
        Exception exception = assertThrows(ValidationException.class, () -> bookingService.create(inputBookingDto, WRONG_ID));
        assertEquals(exception.getMessage(), "fail: item cannot be booked!");
    }

    @Test
    void approve_successfullyApprovedBooking() {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        BookingDto approvedBooking = bookingService.approve(user.getId(), BOOKING_ID, true);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approve_validationFail_bookingAlreadyApproved() {
        Booking appBooking = Booking.builder()
                .booker(booker)
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(item).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(appBooking));
        Exception exception = assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
        assertEquals(exception.getMessage(), "fail: booking is already approved!");
    }

    @ParameterizedTest
    @CsvSource(value = {"ALL, 0, 2", "CURRENT, -2, 2", "PAST, -3, -1", "FUTURE, 2, 4", "WAITING, 0, 1", "REJECTED, 1, 3"})
    void getAllOwnerBookings_successfullyGetList(String state, int startTime, int endTime) {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        LocalDateTime start = LocalDateTime.now().plusDays(startTime);
        LocalDateTime end = LocalDateTime.now().plusDays(endTime);
        Page<Booking> bookingPage = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
        bookingForParamTest.setBooker(user);
        bookingForParamTest.setStart(start);
        bookingForParamTest.setEnd(end);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemForParamTest));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(bookingForParamTest);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),any(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDto> bookings = bookingService.getAllOwnerBookings(user.getId(), state, 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @ParameterizedTest
    @CsvSource(value = {"ALL, 0, 2", "CURRENT, -2, 2", "PAST, -3, -1", "FUTURE, 2, 4", "WAITING, 0, 1", "REJECTED, 1, 3"})
    void getAllUserBookings_successfullyGetList(String state, int startTime, int endTime) {
        when(BookingMapper.toBookingDto(any())).thenReturn(bookingDto);
        LocalDateTime start = LocalDateTime.now().plusDays(startTime);
        LocalDateTime end = LocalDateTime.now().plusDays(endTime);
        Page<Booking> bookingPage = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
        bookingForParamTest.setBooker(user);
        bookingForParamTest.setStart(start);
        bookingForParamTest.setEnd(end);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemForParamTest));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(bookingForParamTest);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),any(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(bookingPage);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDto> bookings = bookingService.getAllUserBookings(user.getId(), state, 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @ParameterizedTest
    @CsvSource(value = "UNSUPPORTED")
    void getAllOwnerBookings_invalidStateFail(String state) {
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(),any(), any(), any())).thenThrow(new InvalidStateException(""));
        Exception exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllOwnerBookings(user.getId(), state, 0, 10));
        assertEquals(exception.getMessage(), "Unknown state: " + state);
    }

    @ParameterizedTest
    @CsvSource(value = "UNSUPPORTED")
    void getAllUserBookings_invalidStateFail(String state) {
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(),any(), any(), any())).thenThrow(new InvalidStateException(""));
        Exception exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllUserBookings(user.getId(), state, 0, 10));
        assertEquals(exception.getMessage(), "Unknown state: " + state);
    }

}
