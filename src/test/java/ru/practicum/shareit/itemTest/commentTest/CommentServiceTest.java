package ru.practicum.shareit.itemTest.commentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentServiceImpl;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

    @Mock
     CommentRepository commentRepository;

    @InjectMocks
     CommentServiceImpl commentService;

     static final Long COMMENT_ID = 1L;
     static final Long USER_ID = 1L;
     static final Long ITEM_ID = 1L;
     static final Long WRONG_ID = 10L;

    //user
     final User user = new User(USER_ID, "user", "user@user.user");
    //item
     final Item item = new Item(ITEM_ID, "item", "descriptionItem", true, null, user, null, null, null);
    //comment
     final Comment comment = new Comment(COMMENT_ID, "comment", item, user, LocalDateTime.now().minusMinutes(60));
     final List<Comment> commentList = List.of(comment);

    @BeforeEach
    void setUp() {
        when(commentRepository.save(any())).thenReturn(comment);
    }

    @Test
    void getAllCreatedComments_successfullyGetList() {
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong())).thenReturn(commentList);
        List<Comment> list = commentService.getAllCreatedComments(ITEM_ID);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void getAllCreatedComments_notFoundItemId() {
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong())).thenThrow(new NotFoundException("fail: ItemId Not Found"));
        Exception exception = assertThrows(NotFoundException.class, () -> commentService.getAllCreatedComments(WRONG_ID));
        assertEquals(exception.getMessage(), "fail: ItemId Not Found");
    }

    @Test
    void getAllComments_successfullyGetList() {
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentList);
        List<Comment> list = commentService.getAllComments(ITEM_ID);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void getAllComments_notFoundItemId() {
        when(commentRepository.findAllByItemId(anyLong())).thenThrow(new NotFoundException("fail: ItemId Not Found"));
        Exception exception = assertThrows(NotFoundException.class, () -> commentService.getAllComments(WRONG_ID));
        assertEquals(exception.getMessage(), "fail: ItemId Not Found");
    }

}
