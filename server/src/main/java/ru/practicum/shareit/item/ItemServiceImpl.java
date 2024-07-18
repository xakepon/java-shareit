package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public ItemDTO create(ItemDTO itemDTO, Long userId) {
        User user = UserMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = itemDTO.getRequestId() != null ? getItemRequest(itemDTO) : null;

        Item createdItem = ItemMapper.toItem(itemDTO);
        createdItem.setOwner(user);
        createdItem.setComments(Collections.emptyList());
        createdItem.setItemRequest(itemRequest);
        repository.save(createdItem);

        ItemDTO createdItemDTO = ItemMapper.toItemDTO(createdItem);
        log.info("выполнен метод create с параметрами" + " itemDTO:{}, userId:{} / createdItemDTO:{}",
                itemDTO, userId, createdItemDTO);
        return createdItemDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDTO itemDTO = ItemMapper.toItemDTO(item);
        if (item.getOwner().getId().equals(userId)) {
            updateBooking(itemDTO);
        }
        List<CommentDTO> comments = commentService.getAllComments(item.getId())
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(Collectors.toList());
        itemDTO.setComments(comments);
        log.info("выполнен метод getItemById с параметрами" + " itemId:{} , userId:{} / itemDTO:{}", itemId, userId, itemDTO);
        return itemDTO;
    }

    @Override
    public ItemRequest getItemRequest(ItemDTO itemDTO) {
        return itemRequestRepository.findById(itemDTO.getRequestId())
                .orElseThrow(() -> new NotFoundException("Ошибка itemRequestId не найден!"));
    }

    @Override
    public ItemDTO update(ItemDTO itemDTO, Long itemId, Long userId) {
        userService.getById(userId);
        Item itemToUpdate = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка Айтем не найден!"));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new ValidationException("Ошибка не совпадение userId и ownerId !");
        }
        ItemMapper.updateItemDTO(itemDTO, itemToUpdate);
        repository.save(itemToUpdate);

        ItemDTO updatedItemDTO = ItemMapper.toItemDTO(itemToUpdate);
        log.info("выполнен метод save с параметрами" + " itemDTO:{}, itemId:{}, userId:{} / createdItemDTO:{}",
                itemDTO, itemId, userId, updatedItemDTO);
        return updatedItemDTO;
    }

    public ItemDTO getById(Long itemId) {
        ItemDTO itemDTO = ItemMapper.toItemDTO(repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("itemId not Found!")));
        log.info("выполнен метод getById с параметрами" + " itemId: {} / itemDTO: {}", itemId, itemDTO);
        return itemDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAll(Long owner, int from, int size) {
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemDTO> items = repository.findAllByOwnerId(owner, pageRequest)
                .stream()
                .map(ItemMapper::toItemDTO)
                .map(this::updateBooking)
                .map(this::addItemComments)
                .collect(Collectors.toList());

        List<ItemDTO> itemDTOList = getItemDTOList(items);
        log.info("выполнен метод getAll с параметрами" + " items:{}", itemDTOList);
        return itemDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> search(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        int pageNumber = from / size;
        Pageable pageRequest = PageRequest.of(pageNumber, size);

        List<ItemDTO> items = repository.searchItems(text, pageRequest)
                .stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
        log.info("выполнен метод search с параметрами" + " text:{} / items:{}", text, items);
        return items;
    }

    @Override
    public CommentDTO createComment(Long itemId, Long userId, CommentDTO commentDTO) {
        if (commentDTO.getText().isEmpty()) {
            throw new ValidationException("Ошибка - отзыв пустой!");
        }
        Item item = getItem(itemId);
        User user = UserMapper.toUser(userService.getById(userId));
        validateBookingExist(itemId, userId);

        Comment comment = CommentMapper.toComment(commentDTO);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        CommentDTO createdCommentDTO = CommentMapper.toCommentDTO(comment);
        log.info("выполнен метод createComment с параметрами" + " itemId:{}, userId:{}, commentDTO:{} / createdCommentDTO:{}",
                itemId, userId, commentDTO, createdCommentDTO);
        return createdCommentDTO;
    }

    private Item getItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка - номер отзыва не найден!"));
    }

    private ItemDTO updateBooking(ItemDTO itemDTO) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemDTO.getId());

        Booking lastBooking = getLastBooking(now, bookings);
        Booking nextBooking = getNextBooking(now, bookings);
        itemDTO.setLastBooking(lastBooking != null ? BookingMapper.toItemBookingDTO(lastBooking) : null);
        itemDTO.setNextBooking(nextBooking != null ? BookingMapper.toItemBookingDTO(nextBooking) : null);
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
                .map(CommentMapper::toCommentDTO)
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

}
