package ru.practicum.shareit.itemTest.commentTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {


     MockedStatic<ItemMapper> itemMapper;

    @InjectMocks
     CommentMapper commentMapper;

     static final Long COMMENT_ID = 1L;
     static final Long USER_ID = 1L;
     static final Long ITEM_ID = 1L;
     static final Long REQUEST_ID = 1L;

     final User user = new User(USER_ID, "user", "user@user.user");
     final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
     final ItemDto itemDto = new ItemDto(ITEM_ID, REQUEST_ID, "item", "descriptionItem", true, null, null, user, null);

     final Comment comment = Comment.builder()
            .id(COMMENT_ID)
            .text("text")
            .item(item)
            .author(user)
            .created(LocalDateTime.now().minusMinutes(1))
            .build();
     final CommentDTO commentDto = CommentDTO.builder()
            .id(COMMENT_ID)
            .text("text")
            .item(itemDto)
            .authorName(user.getName())
            .created(LocalDateTime.now().minusMinutes(1))
            .build();

    @Test
    void toCommentDto_successfully() {
        CommentDTO actCommentDto = commentMapper.toCommentDto(comment);
        assertEquals(actCommentDto.getText(), commentDto.getText());
    }

    @Test
    void toComment_successfully() {
        Comment actComment = commentMapper.toComment(commentDto);
        assertEquals(actComment.getText(), comment.getText());
    }

}
