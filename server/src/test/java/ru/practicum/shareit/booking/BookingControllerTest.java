package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RandomUtils;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class BookingControllerTest extends BasicControllerTest {

    @Test
    void createBookingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings/" + booking.getId(), bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()))
                .andExpect(jsonPath("$.item.id").value(item.getId()));
    }

    @Test
    void updateBookingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(APPROVED.name()));
    }

    @Test
    void updateBookingByNotOwnerTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        User otherUser = createUser();
        MultiValueMap<String, String> otherUserHeaders = createHeaders(USER_ID_HEADER, otherUser.getId().toString());
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", otherUserHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void getBookingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings/" + booking.getId(), bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()));
    }

    @Test
    void getBookingsForUserTest() throws Exception {
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings", bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsForOwnerTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings/owner", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createBookingForUnavailableItemTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item unavailableItem = createItem(ownerHeaders, false);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        String jsonBooking = createJson(bookingDtoToMap(RandomUtils.getBooking(unavailableItem.getId(), start, end)));
        performRequest(POST, "/bookings", jsonBooking, bookerHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void createBookingWrongUserIdTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        MultiValueMap<String, String> wrongUserHeaders = createHeaders(USER_ID_HEADER, "999");
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        String jsonBooking = createJson(bookingDtoToMap(RandomUtils.getBooking(item.getId(), start, end)));
        performRequest(POST, "/bookings", jsonBooking, wrongUserHeaders)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getNonExistingBookingTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        performRequest(GET, "/bookings/999", headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void updateBookingByWrongUserTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        MultiValueMap<String, String> wrongUserHeaders = createHeaders(USER_ID_HEADER, "999");
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", wrongUserHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void getBookingsByUserCurrentTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);
        performRequest(GET, "/bookings?state=" + CURRENT, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserFutureTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings?state=" + FUTURE, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserPastTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusSeconds(3);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings?state=" + PAST, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBookingsByUserWaitingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings?state=" + WAITING, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserRejectedTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=false", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(REJECTED.name()));
        performRequest(GET, "/bookings?state=" + REJECTED, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(REJECTED.name()));
    }

    @Test
    void getBookingsForOwnerCurrentTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);
        performRequest(GET, "/bookings/owner?state=" + CURRENT, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerPastTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusSeconds(3);
        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);
        performRequest(GET, "/bookings/owner?state=" + PAST, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerFutureTimeTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings/owner?state=" + FUTURE, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerWaitingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(GET, "/bookings/owner?state=" + WAITING, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerRejectedTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=false", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(REJECTED.name()));
        performRequest(GET, "/bookings/owner?state=" + REJECTED, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(REJECTED.name()));
    }

}