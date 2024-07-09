package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RequestMapperTest {

   /* @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;*/

   // @InjectMocks
    //private ItemRequestMapper itemRequestMapper;


    private static final Long REQUEST_ID = 1L;
    private static final Long USER_ID = 1L;

    private final User user = new User(USER_ID, "user", "user@user.user");
    private final UserDTO userDto = new UserDTO(USER_ID, "user", "user@user.user");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(REQUEST_ID)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.now().minusMinutes(60))
            .items(null)
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(REQUEST_ID)
            .description("description")
            .requestor(userDto)
            .created(LocalDateTime.now().minusMinutes(60))
            .items(null)
            .build();

    @Test
    void toItemRequestDto_successfully() {
        ItemRequestDto actRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(actRequestDto.getId(), itemRequestDto.getId());
        assertEquals(actRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void toItemRequest_successfully() {
        ItemRequest actRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        assertEquals(actRequest.getId(), itemRequest.getId());
        assertEquals(actRequest.getDescription(), itemRequest.getDescription());
    }

}
