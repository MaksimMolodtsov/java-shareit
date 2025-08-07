package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest extends BasicControllerTest<BookingClient> {

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void createBookingAndStatusOk() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<BookingCreateDto> bookingCreateDtoCaptor = ArgumentCaptor.forClass(BookingCreateDto.class);
        verify(client).createBooking(Mockito.eq(1L), bookingCreateDtoCaptor.capture());
        BookingCreateDto capturedDto = bookingCreateDtoCaptor.getValue();
        assertEquals(bookingCreateDto.getItemId(), capturedDto.getItemId());
    }

    @Test
    void updateBookingAndStatusOk() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
        verify(client).updateBookingById(1L, 1L, true);
    }

    @Test
    void createBookingForMissingHeaderAndStatusBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingByIdAndStatusOk() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(client).getBookingById(1L, 1L);
    }

    @Test
    void getBookingsForUserAndStatusOk() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
        verify(client).getBookingsForUser(1L, BookingState.ALL);
    }

    @Test
    void getBookingsForOwnerAndStatusOk() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
        verify(client).getBookingsForOwner(1L, BookingState.ALL);
    }

    @Test
    void createBookingForPastStartDate() throws Exception {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createBookingForEmptyItemId() throws Exception {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createBookingForHeaderUserIdIsZero() throws Exception {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateBookingForMissingApprovedParam() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

   @Test
    void updateBookingForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 0)
                        .param("approved", "true"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateBookingForBookingIdIsZero() throws Exception {
        mockMvc.perform(patch("/bookings/0")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void getBookingsByUserForInvalidState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingForMissingHeader() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingForBookingIdIsNegative() throws Exception {
        mockMvc.perform(get("/bookings/-1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingByOwnerForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(get("/bookings/owner", 1L)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {
        when(client.createBooking(anyLong(), any(BookingCreateDto.class)))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isInternalServerError());
    }

}