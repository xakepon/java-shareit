package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.ItemDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    private String text;
    private ItemDTO item;
    private String authorName;
    private LocalDateTime created;

}
