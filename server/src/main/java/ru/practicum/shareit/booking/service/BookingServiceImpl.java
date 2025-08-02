package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService,
                              ItemService itemService, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingCreateDto.getItemId());
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна для бронирования");
        }
        Booking booking = bookingMapper.toBooking(bookingCreateDto);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        log.info("Создано бронирование: {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с указанным id: {} не найдено", bookingId));
        if (booking.getUser().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new NotValidException("Вы не являетесь владельцем вещи или тем, кто ее забронировал");
        }
    }

    @Override
    @Transactional
    public Booking updateBookingById(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBookingById(userId, bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotValidException(
                    "Вы не являетесь владельцем вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsForUser(Long userId, BookingState state) {
        userService.getUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        if (isNull(state)) {
            state = BookingState.ALL;
        }
        return switch (state) {
            case CURRENT -> bookingRepository.findByUserIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case PAST -> bookingRepository.findByUserIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findByUserIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookingRepository.findByUserIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByUserIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case ALL -> bookingRepository.findByUserId(userId, sort);
        };
    }

    @Override
    public List<Booking> getBookingsForOwner(Long userId, BookingState state) {
        userService.getUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        if (isNull(state)) {
            state = BookingState.ALL;
        }
        return switch (state) {
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case ALL -> bookingRepository.findByItemOwnerId(userId, sort);
        };
    }

    @Override
    public Booking getLastBooking(Long itemId) {
        return bookingRepository.findByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now());
    }

    @Override
    public Booking getNextBooking(Long itemId) {
        return bookingRepository.findByItemIdAndStartIsAfterOrderByStartAsc(itemId, LocalDateTime.now());
    }

    @Override
    public boolean existsByBookerIdAndItemId(Long bookerId, Long itemId) {
        return bookingRepository.existsByUser_IdAndItemIdAndEndBefore(bookerId, itemId, LocalDateTime.now());
    }

}