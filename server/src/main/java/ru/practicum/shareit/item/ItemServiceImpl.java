package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
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
    private UserMapper userMapper;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;
    private BookingMapper bookingMapper;

    @Override
    public ItemDTO create(ItemDTO itemDTO, Long userId) {
        User user = userMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = itemDTO.getRequestId() != null ? getItemRequest(itemDTO) : null;

        Item createdItem = ItemMapper.toItem(itemDTO);
        createdItem.setOwner(user);
        createdItem.setComments(Collections.emptyList());
        createdItem.setItemRequest(itemRequest);
        repository.save(createdItem);

        ItemDTO createdItemDTO = itemMapper.toItemDTO(createdItem);
        log.info("method: create |Request/Response|" + " itemDTO:{}, userId:{} / createdItemDTO:{}",
                itemDTO, userId, createdItemDTO);
        return createdItemDTO;
    }

    @Override
    public ItemDTO getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDTO itemDTO = itemMapper.toItemDTO(item);
        if (item.getOwner().getId().equals(userId)) {
            updateBooking(itemDTO);
        }
        List<CommentDTO> comments = commentService.getAllComments(item.getId())
                .stream()
                .map(commentMapper::toCommentDTO)
                .collect(Collectors.toList());
        itemDTO.setComments(comments);
        log.info("method: getById |Request/Response|" + " itemId:{} , userId:{} / itemDTO:{}", itemId, userId, itemDTO);
        return itemDTO;
    }

    @Override
    public ItemRequest getItemRequest(ItemDTO itemDTO) {
        return itemRequestRepository.findById(itemDTO.getRequestId())
                .orElseThrow(() -> new NotFoundException("fail: itemRequestId Not Found!"));
    }

    @Override
    public ItemDTO update(ItemDTO itemDTO, Long itemId, Long userId) {
        userService.getById(userId);
        Item itemToUpdate = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("fail: item Not Found!"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new ValidationException("fail: ownerId and userId is not equals!");
        }
        itemMapper.updateItemDTO(itemDTO, itemToUpdate);
        repository.save(itemToUpdate);

        ItemDTO updatedItemDTO = itemMapper.toItemDTO(itemToUpdate);
        log.info("method: save |Request/Response|" + " itemDTO:{}, itemId:{}, userId:{} / createdItemDTO:{}",
                itemDTO, itemId, userId, updatedItemDTO);
        return updatedItemDTO;
    }

    public ItemDTO getById(Long itemId) {
        ItemDTO itemDTO = itemMapper.toItemDTO(repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("itemId not Found!")));
        log.info("method: getById |Request/Response|" + " itemId: {} / itemDTO: {}", itemId, itemDTO);
        return itemDTO;
    }

    @Override
    public List<ItemDTO> getAll(Long owner, int from, int size) {
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemDTO> items = repository.findAllByOwnerId(owner, pageRequest)
                .stream()
                .map(itemMapper::toItemDTO)
                .map(this::updateBooking)
                .map(this::addItemComments)
                .collect(Collectors.toList());

        List<ItemDTO> itemDTOList = getItemDTOList(items);
        log.info("method: getAll |Response|" + " items:{}", itemDTOList);
        return itemDTOList;
    }

    @Override
    public List<ItemDTO> search(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        List<ItemDTO> items = repository.searchItems(text, pageRequest)
                .stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
        log.info("method: search |Request/Response|" + " text:{} / items:{}", text, items);
        return items;
    }

    @Override
    public CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDTO) {
        if (commentDTO.getText().isEmpty()) {
            throw new ValidationException("fail: comment is Empty!");
        }
        Item item = getItem(itemId);
        User user = userMapper.toUser(userService.getById(userId));
        validateBookingExist(itemId, userId);

        Comment comment = commentMapper.toComment(commentDTO);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        CommentDTO createdCommentDTO = commentMapper.toCommentDTO(comment);
        log.info("method: createComment |Request/Response|" + " itemId:{}, userId:{}, commentDTO:{} / createdCommentDTO:{}",
                itemId, userId, commentDTO, createdCommentDTO);
        return createdCommentDTO;
    }

    private Item getItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("fail: itemId Not Found!"));
    }

    private ItemDTO updateBooking(ItemDTO itemDTO) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemDTO.getId());

        Booking lastBooking = getLastBooking(now, bookings);
        Booking nextBooking = getNextBooking(now, bookings);
        itemDTO.setLastBooking(lastBooking != null ? bookingMapper.toItemBookingDTO(lastBooking) : null);
        itemDTO.setNextBooking(nextBooking != null ? bookingMapper.toItemBookingDTO(nextBooking) : null);
        return itemDTO;
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

    private ItemDTO addItemComments(ItemDTO itemDTO) {
        List<CommentDTO> comments = getCommentDTOList(commentService.getAllCreatedComments(itemDTO.getId()));
        itemDTO.setComments(comments);
        return itemDTO;
    }

    private List<ItemDTO> getItemDTOList(List<ItemDTO> items) {
        List<ItemDTO> itemDTOList = new ArrayList<>();
        items.stream()
                .map(this::updateBooking)
                .forEach(item -> {
                    getCommentDTOList(commentService.getAllCreatedComments(item.getId()));
                    itemDTOList.add(item);
                });
        return itemDTOList;
    }

    private List<CommentDTO> getCommentDTOList(List<Comment> comments) {
        return comments.stream()
                .map(commentMapper::toCommentDTO)
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
