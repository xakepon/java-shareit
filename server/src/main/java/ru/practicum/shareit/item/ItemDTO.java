package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.ShortBookingDTO;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDTO {

    private Long id;
    private Long requestId;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingDTO lastBooking;
    private ShortBookingDTO nextBooking;
    private User owner;
    private List<CommentDTO> comments;

}
