package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.booking.*;

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
    private CommentRepository commentRepository;
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;
    private CommentService commentService;
    //private UserMapper userMapper;
    //private ItemMapper itemMapper;
    //private CommentMapper commentMapper;
    private BookingMapper bookingMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = UserMapper.toUser(userService.get(userId));
        ItemRequest itemRequest = itemDto.getRequestId() != null ? getItemRequest(itemDto) : null;

        Item createdItem = ItemMapper.toItem(itemDto);
        createdItem.setOwner(user);
        createdItem.setComments(Collections.emptyList());
        createdItem.setItemRequest(itemRequest);
        repository.save(createdItem);

        ItemDto createdItemDto = ItemMapper.toItemDto(createdItem);
        log.info("выполнен метод create с параметрами" + " itemDto:{}, userId:{} / createdItemDto:{}",
                itemDto, userId, createdItemDto);
        return createdItemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userService.get(userId);

        Item itemToUpdate = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка Айтем не найден!"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new ValidationException("Ошибка не совпадение userId и ownerId !");
        }
        ItemMapper.updateItemDto(itemDto, itemToUpdate);
        repository.save(itemToUpdate);

        ItemDto updatedItemDto = ItemMapper.toItemDto(itemToUpdate);
        log.info("выполнен метод save с параметрами" + " itemDto:{}, itemId:{}, userId:{} / createdItemDto:{}",
                itemDto, itemId, userId, updatedItemDto);
        return updatedItemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            updateBooking(itemDto);
        }

        List<CommentDTO> comments = commentService.getAllComments(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        log.info("выполнен метод getById с параметрами" + " itemId:{} , userId:{} / itemDto:{}", itemId, userId, itemDto);
        return itemDto;
    }

    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId) {
        ItemDto itemDto = ItemMapper.toItemDto(repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("itemId не найден!")));
        log.info("выполнен метод getById с параметрами" + " itemId: {} / itemDto: {}", itemId, itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long owner, int from, int size) {
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        List<ItemDto> items = repository.findAllByOwnerId(owner, pageRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .map(this::updateBooking)
                .map(this::addItemComments)
                .collect(Collectors.toList());

        List<ItemDto> itemDtoList = getItemDtoList(items);
        log.info("выполнен метод getAll с параметрами" + " items:{}", itemDtoList);
        return itemDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        List<ItemDto> items = repository.searchItems(text, pageRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("выполнен метод search с параметрами" + " text:{} / items:{}", text, items);
        return items;
    }

    @Override
    public CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Ошибка - отзыв пустой!");
        }
        Item item = getItem(itemId);
        User user = UserMapper.toUser(userService.get(userId));
        validateBookingExist(itemId, userId);

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        CommentDTO createdCommentDto = CommentMapper.toCommentDto(comment);
        log.info("выполнен метод createComment с параметрами" + " itemId:{}, userId:{}, commentDto:{} / createdCommentDto:{}",
                itemId, userId, commentDto, createdCommentDto);
        return createdCommentDto;
    }

    private Item getItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка - номер отзыва не найден!"));
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
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private void validateBookingExist(Long itemId, Long userId) {
        if (!hasUserBookedAndFinishedTheItem(itemId, userId)) {
            throw new ValidationException("Ошибка - бронирование для пользователя и предмета не корректные!");
        }
    }

    private boolean hasUserBookedAndFinishedTheItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        return !bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now());
    }

    @Override
    public ItemRequest getItemRequest(ItemDto itemDto) {
        return itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Ошибка itemRequestId не найден!"));
    }

}
