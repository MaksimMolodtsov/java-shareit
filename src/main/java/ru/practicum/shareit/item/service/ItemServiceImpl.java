package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CommentRepository commentRepository,
                           @Lazy BookingService bookingService, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public Item createItem(Long userId, ItemCreateDto itemCreateDto) {
        User user = userService.getUserById(userId);
        Item item = itemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        log.info("Создана вещь: {}", item);
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID {} не найдена", itemId));
    }

    @Override
    public List<Item> getItemsByOwnerId(Long userId) {
        userService.getUserById(userId);
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional
    public Item updateItemById(Long userId, Long itemId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = this.getItemById(itemId);
        if (user.equals(item.getOwner())) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
        }
        log.info("Данные обновлены для вещи: {}", item);
        return itemRepository.save(item);
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Override
    @Transactional
    public Comment createComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        List<Booking> bookings = bookingService.getBookingsForUser(userId, BookingState.PAST);

        boolean hasBooked = bookings.stream().anyMatch(booking -> booking.getItem().getId().equals(itemId));
        if (!hasBooked) {
            throw new NotValidException("Пользователь не может оставлять комментарии, если не брал вещь в аренду");
        }
        Comment comment = Comment.builder()
                .text(commentCreateDto.getText())
                .item(item)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        log.info("Создан комментарий: {}", comment);
        return commentRepository.save(comment);
    }

}
