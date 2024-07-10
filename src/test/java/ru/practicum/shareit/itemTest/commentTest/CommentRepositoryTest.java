package ru.practicum.shareit.itemTest.commentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
     CommentRepository commentRepository;

    @Autowired
     UserRepository userRepository;

    @Autowired
     ItemRepository itemRepository;

     static final Long USER_ID = 1L;
     static final Long ITEM_ID = 1L;

     User user;
     Item item;
     Comment comment1;
     Comment comment2;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID, "user", "user@user.com");
        item = new Item(ITEM_ID, "Arduino", "Arduino", true, null, user, null, null, null);
        comment1 = new Comment(null, "First comment", item, user, LocalDateTime.now().minusDays(1));
        comment2 = new Comment(null, "Second comment", item, user, LocalDateTime.now());
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Test
    void findAllByItemId_successfullyFindComment() {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        assertThat(comments.size(), equalTo(2));
        assertThat(comments.get(0), equalTo(comment2));
        assertThat(comments.get(1), equalTo(comment1));
    }

    @Test
    void findAllByItemId_successfullyFindItem() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments.size(), equalTo(2));
        assertThat(comments, containsInAnyOrder(comment1, comment2));
    }

}
