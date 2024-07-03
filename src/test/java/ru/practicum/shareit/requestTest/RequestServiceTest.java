package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
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
public class RequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long REQUEST_ID = 1L;

    //User
    private final User requestor = new User(USER_ID, "user", "user@user.user");
    private final UserDTO requestorDto = new UserDTO(USER_ID, "user", "user@user.user");
    //Item
    private final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, requestor, null, null, null);
    private final ItemDto itemDto = new ItemDto(ITEM_ID, USER_ID, "item", "descriptionItem", item.getAvailable(), null, null, requestor, null);
    //Request
    private final ItemRequest itemRequest = new ItemRequest(REQUEST_ID, "description", requestor, LocalDateTime.now(), List.of(item));
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(REQUEST_ID, "description", requestorDto, itemRequest.getCreated(),  List.of(itemDto));
    private final List<ItemRequest> itemRequestList = List.of(itemRequest);
    private final List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto);

    @BeforeEach
    void setUp() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRequestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
    }

    @Test
    void create_successfullyCreated() {
        when(itemRequestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
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
