package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String PATH = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingCreateDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateBookingById(Long userId, Long bookingId, boolean approved) {
        String path = UriComponentsBuilder.fromPath("/{bookingId}")
                .queryParam("approved", approved)
                .buildAndExpand(bookingId)
                .toUriString();

        return patch(path, userId);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsForUser(Long userId, BookingState state) {
        String path = UriComponentsBuilder.fromPath("")
                .queryParam("state", state.name())
                .toUriString();

        return get(path, userId, null);
    }

    public ResponseEntity<Object> getBookingsForOwner(Long userId, BookingState state) {
        String path = UriComponentsBuilder.fromPath("/owner")
                .queryParam("state", state.name())
                .toUriString();

        return get(path, userId);
    }

}