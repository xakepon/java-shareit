package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;


import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllComments(Long itemId) {
        if (itemId == null) {
            throw new NotFoundException("fail: ItemId Not Found");
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        log.info("Выполнение метода getAllComments с параметрами " + "itemDtoId:{} / comments:{}", itemId, comments);
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllCreatedComments(Long itemId) {
        if (itemId == null) {
            throw new NotFoundException("fail: ItemId Not Found");
        }
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreated(itemId);
        log.info("Выполнение метода getAllCreatedComments с параметрами " + "itemDtoId:{} / comments:{}", itemId, comments);
        return comments;
    }

}
