package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long id;

    @NotBlank
    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}
