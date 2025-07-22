package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        Booking booking = bookingService.createBooking(userId, bookingCreateDto);
        return bookingMapper.toBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        Booking booking = bookingService.approveBooking(userId, bookingId, approved);
        return bookingMapper.toBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    @GetMapping
    public List<BookingDto> getBookingsForUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        List<Booking> bookings = bookingService.getBookingsForUser(userId, state);
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        List<Booking> bookings = bookingService.getBookingsForOwner(userId, state);
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
