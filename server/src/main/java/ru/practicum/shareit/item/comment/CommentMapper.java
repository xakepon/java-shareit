package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;


@AllArgsConstructor
public class CommentMapper {

    public static CommentDTO toCommentDTO(Comment comment) {
        return comment == null ? null : CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(ItemMapper.toItemDTO(comment.getItem()))
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDTO commentDto) {
        return commentDto == null ? null : Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

}
