package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceTest {

    @Mock
     ItemRepository itemRepository;
    @Mock
     BookingRepository bookingRepository;
    @Mock
     UserService userService;
    @Mock
     ItemRequestRepository itemRequestRepository;
    @Mock
     CommentRepository commentRepository;
    @Mock
     CommentService commentService;

     MockedStatic<UserMapper> userMapper;

     MockedStatic<ItemMapper> itemMapper;

     MockedStatic<BookingMapper> bookingMapper;

     MockedStatic<ItemRequestMapper> itemRequestMapper;

    @InjectMocks
     ItemServiceImpl itemService;

     CommentMapper commentMapper;

     static final Long USER_ID = 1L;
     static final Long ITEM_ID = 1L;
     static final Long ITEM_ID_2 = 2L;
     static final Long REQUEST_ID = 1L;
     static final Long REQUEST_ID_2 = 2L;
     static final Long BOOKING_ID = 1L;
     static final Long COMMENT_ID = 1L;
     static final Long WRONG_USER_ID = 10L;
     static final Long WRONG_ITEM_ID = 10L;

    //Users
     final User user = new User(USER_ID, "user", "user@user.user");
     final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");
    //Items
     final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
     final Item item2 = new Item(ITEM_ID_2, "item", "descriptionItem", true, null, user, null, null, null);
     final ItemDto itemDto = new ItemDto(ITEM_ID, REQUEST_ID_2, "item", "descriptionItem", true, null, null, user, null);
     final ItemDto itemDto2 = new ItemDto(ITEM_ID_2, REQUEST_ID, "item", "descriptionItem", true, null, null, user, null);
     final Item updatedItem = new Item(ITEM_ID, "name", "description", true, null, user, null, null, null);
     final ItemDto updatedItemDto = new ItemDto(ITEM_ID, REQUEST_ID, "name", "description", null, null, null, user, null);
    //Requests
     final ItemRequest itemRequest = new ItemRequest(REQUEST_ID, "description", user, LocalDateTime.now(), List.of(item));
     final ItemRequestDto itemRequestDto = new ItemRequestDto(REQUEST_ID, "description", userDto, LocalDateTime.now(), List.of(itemDto));
    //Bookings
     final Booking booking = new Booking(BOOKING_ID, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, BookingStatus.APPROVED);
     final List<Booking> bookingList = List.of(booking);
    //Comments
     final Comment comment = new Comment(COMMENT_ID, "comment", item, user, LocalDateTime.now().minusMinutes(60));
     final CommentDTO commentDto = CommentDTO.builder().id(COMMENT_ID).text("comment").item(itemDto).authorName("user").build();

    @Test
    void create_successfullyCreated() {
        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
            when(ItemMapper.toItem(any(ItemDto.class))).thenReturn(item);
            when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
            assertEquals(itemDto, itemService.create(itemDto, user.getId()));
            verify(itemRepository).save(item);
        }

    }

    @Test
    void create_notFoundUserId() {
        when(userService.get(anyLong())).thenThrow(new NotFoundException("fail: user/owner ID Not Found!"));
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.create(itemDto, WRONG_USER_ID));
        assertEquals(exception.getMessage(), "fail: user/owner ID Not Found!");
    }

    @Test
    void create_notFoundItemRequest() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.getItemRequest(itemDto2)).thenThrow(new NotFoundException("fail: itemRequestId Not Found!"));
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.getItemRequest(itemDto2));
        assertEquals(exception.getMessage(), "fail: itemRequestId Not Found!");
    }

    @Test
    void update_successfullyUpdated() {
        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(updatedItem));
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(updatedItemDto);
            assertEquals(updatedItemDto, itemService.update(updatedItemDto, user.getId(), item.getId()));
            verify(itemRepository).save(any(Item.class));
        }
    }

    @Test
    void update_notFoundItemId() {
        when(itemRepository.findById(anyLong())).thenThrow(new NotFoundException("itemId not Found!"));
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.update(itemDto, WRONG_ITEM_ID, USER_ID));
        assertEquals(exception.getMessage(), "itemId not Found!");
    }

    @Test
    void updateUser_validationUserNotEqualOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Exception exception = assertThrows(ValidationException.class, () -> itemService.update(itemDto, ITEM_ID, WRONG_USER_ID));
        assertEquals(exception.getMessage(), "Ошибка не совпадение userId и ownerId !");
    }

    @Test
    void getById_successfullyGet() {

        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
            assertEquals(itemDto, itemService.getById(item.getId()));
        }
    }

    @Test
    void getItemById_successfullyGet() {

        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
            assertEquals(itemDto, itemService.getItemById(item.getId(), user.getId()));
        }
    }

    @Test
    void getById_notFoundItemId() {
        when(itemRepository.findById(anyLong())).thenThrow(new NotFoundException("itemId not Found!"));
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.getItemById(WRONG_ITEM_ID, USER_ID));
        assertEquals(exception.getMessage(), "itemId not Found!");
    }

    @Test
    void getAll_successfullyGetList() {

        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
            Page<Item> itemsPage = new PageImpl<>(List.of(item));
            Pageable pageable = PageRequest.of(0, 10);
            when(itemRepository.findAllByOwnerId(eq(user.getId()), eq(pageable))).thenReturn(itemsPage);
            List<ItemDto> responseList = itemService.getAll(user.getId(), 0, 10);
            List<ItemDto> expectedList = List.of(itemDto);
            assertEquals(expectedList, responseList);
        }
    }

    @Test
    void search_successfullyGetItem() {

        try (MockedStatic<ItemMapper> mockedStatic = Mockito.mockStatic(ItemMapper.class)) {
            mockedStatic.when(() -> ItemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
            String searchText = "descrip";
            Page<Item> itemsPage = new PageImpl<>(List.of(item));
            Pageable pageable = PageRequest.of(0, 10);
            when(itemRepository.searchItems(eq(searchText), eq(pageable))).thenReturn(itemsPage);
            List<ItemDto> responseList = itemService.search(searchText, 0, 10);
            List<ItemDto> expectedList = List.of(itemDto);
            assertEquals(expectedList, responseList);
        }
    }

    @Test
    void searchEmptyText_ReturnEmptyList() {
        String searchText = "";
        assertThat(itemService.search(searchText, 0, 10), hasSize(0));
        assertThat(itemService.search(null, 0, 10), hasSize(0));
        when(itemRepository.searchItems(anyString(),any())).thenReturn(Page.empty());
        assertEquals(itemService.search("", 0, 10), Collections.EMPTY_LIST);
    }

    @Test
    void createComment_successfullyCreated() {

        try (MockedStatic<CommentMapper> mockedStatic = Mockito.mockStatic(CommentMapper.class)) {
            mockedStatic.when(() -> CommentMapper.toCommentDto(any())).thenReturn(commentDto);
            mockedStatic.when(() -> CommentMapper.toComment(any())).thenReturn(comment);
            when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
            when(userService.get(anyLong())).thenReturn(userDto);
            when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any())).thenReturn(bookingList);
            when(commentRepository.save(any())).thenReturn(comment);
            when(commentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            CommentDTO testComment = itemService.createComment(ITEM_ID, USER_ID, commentDto);
            assertEquals(testComment.getId(), commentDto.getId());
            assertEquals(testComment.getItem(), commentDto.getItem());
            assertEquals(testComment.getText(), commentDto.getText());
            assertEquals(testComment.getAuthorName(), commentDto.getAuthorName());

        }
    }

    @Test
    void createEmptyComment_validationCreationFail() {
        CommentDTO commentDto = new CommentDTO();
        commentDto.setText("");
        assertThrows(ValidationException.class, () -> {
            itemService.createComment(ITEM_ID, USER_ID, commentDto);
        });
    }

}
