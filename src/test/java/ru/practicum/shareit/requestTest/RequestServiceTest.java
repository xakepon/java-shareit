package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestServiceTest {

    @Mock
     ItemRequestRepository itemRequestRepository;
    @Mock
     ItemRepository itemRepository;
    @Mock
     UserService userService;
    @InjectMocks
     ItemRequestServiceImpl itemRequestService;

     static final Long USER_ID = 1L;
     static final Long ITEM_ID = 1L;
     static final Long REQUEST_ID = 1L;

    //User
     final User requestor = new User(USER_ID, "user", "user@user.user");
     final UserDTO requestorDto = new UserDTO(USER_ID, "user", "user@user.user");
    //Item
     final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, requestor, null, null, null);
     final ItemDto itemDto = new ItemDto(ITEM_ID, USER_ID, "item", "descriptionItem", item.getAvailable(), null, null, requestor, null);
    //Request
     final ItemRequest itemRequest = new ItemRequest(REQUEST_ID, "description", requestor, LocalDateTime.now(), List.of(item));
     final ItemRequestDto itemRequestDto = new ItemRequestDto(REQUEST_ID, "description", requestorDto, itemRequest.getCreated(),  List.of(itemDto));
     final List<ItemRequest> itemRequestList = List.of(itemRequest);
     final List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto);

     MockedStatic<ItemRequestMapper> mockedStatic;


    @BeforeEach
    void setUp() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        //when(ItemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        //when(ItemRequestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);

        mockedStatic = Mockito.mockStatic(ItemRequestMapper.class);
        mockedStatic.when(() -> ItemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        mockedStatic.when(() -> ItemRequestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void create_successfullyCreated() {
        ItemRequestDto createdItemRequestDto = itemRequestService.create(USER_ID, itemRequestDto);
        verify(itemRequestRepository).save(any(ItemRequest.class));
        assertEquals(itemRequestDto, createdItemRequestDto);
    }

    @Test
    void getById_successfullyGet() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequestDto foundItemRequestDto = itemRequestService.getById(itemRequest.getId(), USER_ID);
        verify(itemRequestRepository).findById(itemRequest.getId());
        assertEquals(itemRequestDto, foundItemRequestDto);
    }

    @Test
    void getById_NotFoundRequestId() {
        when(userService.get(anyLong())).thenReturn(requestorDto);
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> itemRequestService.getById(10L, 1L));
        assertEquals(exception.getMessage(), "fail: requestId Not Found!");
    }

    @Test
    void getOwnItemRequests_successfullyGetList() {
        when(itemRequestRepository.findAllByRequestorId(anyLong(), any(Pageable.class))).thenReturn(itemRequestList);
        List<ItemRequestDto> foundItemRequestDtos = itemRequestService.getOwnItemRequests(USER_ID, 0, 10);
        assertEquals(itemRequestDtoList, foundItemRequestDtos);
    }

    @Test
    void getAllItemRequests_successfullyGetList() {
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong(), any(Pageable.class))).thenReturn(itemRequestList);
        List<ItemRequestDto> foundItemRequestDtos = itemRequestService.getAllItemRequests(USER_ID, 0, 10);
        assertEquals(itemRequestDtoList, foundItemRequestDtos);
    }

}
