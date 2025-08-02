package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingClient;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                     @PathVariable @Positive Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingClient.updateBookingById(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                     @PathVariable @Positive Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForUser(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getBookingsForOwner(userId, state);
    }

}
