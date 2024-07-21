package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemIdOrderByCreated(Long itemId);

    List<Comment> findAllByItemId(Long itemId);

}
