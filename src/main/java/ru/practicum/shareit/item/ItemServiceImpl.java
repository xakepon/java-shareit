package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDTO;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.booking.*;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private ItemRepository repository;
    private BookingRepository bookingRepository;
    private UserService userService;
    private CommentService commentService;
    private UserMapper userMapper;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;
    private BookingMapper bookingMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userMapper.toUser(userService.get(userId));
        Item createdItem = itemMapper.toItem(itemDto);
        createdItem.setOwner(user);
        createdItem.setComments(Collections.emptyList());
        repository.save(createdItem);

        ItemDto createdItemDto = itemMapper.toItemDto(createdItem);
        log.info("method: create |Request/Response|" + " itemDto:{}, userId:{} / createdItemDto:{}",
                itemDto, userId, createdItemDto);
        return createdItemDto;
    }

    @Override
    public ItemDto save(ItemDto itemDto, Long itemId, Long userId) {
        userService.get(userId);

        Item itemToUpdate = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("fail: item Not Found!"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new ValidationException("fail: ownerId and userId is not equals!");
        }
        itemMapper.updateItemDto(itemDto, itemToUpdate);
        repository.save(itemToUpdate);

        ItemDto updatedItemDto = itemMapper.toItemDto(itemToUpdate);
        log.info("method: save |Request/Response|" + " itemDto:{}, itemId:{}, userId:{} / createdItemDto:{}",
                itemDto, itemId, userId, updatedItemDto);
        return updatedItemDto;
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            updateBooking(itemDto);
        }

        List<CommentDTO> comments = commentService.getAllComments(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        log.info("method: getById |Request/Response|" + " itemId:{} , userId:{} / itemDto:{}", itemId, userId, itemDto);
        return itemDto;
    }

    public ItemDto getById(Long itemId) {
        ItemDto itemDto = itemMapper.toItemDto(repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("itemId not Found!")));
        log.info("method: getById |Request/Response|" + " itemId: {} / itemDto: {}", itemId, itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(Long owner) {
        List<ItemDto> items = repository.findAllByOwnerId(owner)
                .stream()
                .map(itemMapper::toItemDto)
                .map(this::updateBooking)
                .map(this::addItemComments)
                .collect(Collectors.toList());

        List<ItemDto> itemDtoList = getItemDtoList(items);
        log.info("method: getAll |Response|" + " items:{}", itemDtoList);
        return itemDtoList;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<ItemDto> items = repository.searchItems(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("method: search |Request/Response|" + " text:{} / items:{}", text, items);
        return items;
    }

    @Override
    public CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("fail: comment is Empty!");
        }
        Item item = getItem(itemId);
        User user = userMapper.toUser(userService.get(userId));
        validateBookingExist(itemId, userId);

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentService.saveComment(comment);

        CommentDTO createdCommentDto = commentMapper.toCommentDto(comment);
        log.info("method: createComment |Request/Response|" + " itemId:{}, userId:{}, commentDto:{} / createdCommentDto:{}",
                itemId, userId, commentDto, createdCommentDto);
        return createdCommentDto;
    }

    private Item getItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("fail: itemId Not Found!"));
    }

    private Booking getLastBooking(LocalDateTime now, List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking getNextBooking(LocalDateTime now, List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private ItemDto updateBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemDto.getId());

        Booking lastBooking = getLastBooking(now, bookings);
        Booking nextBooking = getNextBooking(now, bookings);

        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    private ItemDto addItemComments(ItemDto itemDto) {
        List<CommentDTO> comments = getCommentDtoList(commentService.getAllCreatedComments(itemDto.getId()));
        itemDto.setComments(comments);
        return itemDto;
    }

    private List<ItemDto> getItemDtoList(List<ItemDto> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        items.stream()
                .map(this::updateBooking)
                .forEach(item -> {
                    getCommentDtoList(commentService.getAllCreatedComments(item.getId()));
                    itemDtoList.add(item);
                });
        return itemDtoList;
    }

    private List<CommentDTO> getCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private void validateBookingExist(Long itemId, Long userId) {
        if (!hasUserBookedAndFinishedTheItem(itemId, userId)) {
            throw new ValidationException("fail: booking for user and item Not Valid!");
        }
    }

    private boolean hasUserBookedAndFinishedTheItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        return !bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now());
    }

}
