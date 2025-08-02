package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    Booking createBooking(Long userId, BookingCreateDto bookingCreateDto);

    Booking getBookingById(Long userId, Long bookingId);

    Booking updateBookingById(Long userId, Long bookingId, boolean approved);

    List<Booking> getBookingsForUser(Long userId, BookingState state);

    List<Booking> getBookingsForOwner(Long userId, BookingState state);

    Booking getLastBooking(Long itemId);

    Booking getNextBooking(Long itemId);

    boolean existsByBookerIdAndItemId(Long bookerId, Long itemId);

}