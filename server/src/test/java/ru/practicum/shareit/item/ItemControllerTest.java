package ru.practicum.shareit.item;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.BasicControllerTest;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RandomUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class ItemControllerTest extends BasicControllerTest {

    @Test
    void createItemTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        ItemDto itemDto = RandomUtils.getRandomItem();
        String itemDtoJson = createJson(itemDtoToMap(itemDto));
        performRequest(POST, "/items", itemDtoJson, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItemByIdTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        Item item = createItem(headers, true);
        String updatedItemJson = createJson(Map.of(
                "name", "Updated Item",
                "description", "Updated Description",
                "available", false
        ));
        performRequest(PATCH, "/items/" + item.getId(), updatedItemJson, headers)
                .andExpect(status().isOk());
        performRequest(GET, "/items/" + item.getId(), headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").isBoolean());
    }

    @Test
    void createItemWithoutUserHeaderTest() throws Exception {
        String itemJson = createJson(Map.of(
                "name", "Item",
                "description", "Description",
                "available", true
        ));
        performRequest(POST, "/items", itemJson)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void updateNonExistItemTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        String updatedItemJson = createJson(Map.of(
                "name", "Updated Item",
                "description", "Updated Description",
                "available", false
        ));
        performRequest(PATCH, "/items/999", updatedItemJson, headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getItemByIdTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        Item item = createItem(headers, true);
        performRequest(GET, "/items/" + item.getId(), headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
    }

    @Test
    void getNonExistentItemByIdTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        performRequest(GET, "/items/999", headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getItemsForOwnerTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        Item item1 = createItem(headers, true);
        Item item2 = createItem(headers, true);
        performRequest(GET, "/items", headers)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(item2.getAvailable()));
    }

    @Test
    void searchItemsTest() throws Exception {
        User user = createUser();
        Item item = createItem(createHeaders(USER_ID_HEADER, user.getId().toString()), true);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        params.add("text", item.getName().substring(0, 3));
        performRequest(GET, "/items/search", params, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(item.getName()));
    }

    @Test
    void searchItemsEmptyRequestTest() throws Exception {
        User user = createUser();
        createItem(createHeaders(USER_ID_HEADER, user.getId().toString()), true);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        params.add("text", "");
        performRequest(GET, "/items/search", params, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createCommentWithoutBookingTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User otherUser = createUser();
        MultiValueMap<String, String> otherUserHeaders = createHeaders(USER_ID_HEADER, otherUser.getId().toString());
        String commentText = "Комментарий";
        performRequest(POST, "/items/" + item.getId() + "/comment",
                createJson(Map.of("text", commentText)), otherUserHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void createItemWithRequestTest() throws Exception {
        User requester = createUser();
        MultiValueMap<String, String> requesterHeaders = createHeaders(USER_ID_HEADER, requester.getId().toString());
        String requestJson = createJson(Map.of(
                "description", "Нужен молоток"
        ));
        String requestResponse = performRequest(POST, "/requests", requestJson, requesterHeaders)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long requestId = objectMapper.readTree(requestResponse).path("id").asLong();
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        String itemJson = createJson(Map.of(
                "name", "Name",
                "description", "Description",
                "available", true,
                "requestId", requestId
        ));
        performRequest(POST, "/items", itemJson, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.request").value(requestId));
    }

    @Test
    void createItemWithoutRequestTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        ItemDto itemDto = RandomUtils.getRandomItem();
        itemDto.setRequest(null);
        String itemDtoJson = createJson(itemDtoToMap(itemDto));
        performRequest(POST, "/items", itemDtoJson, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }

    @Test
    void createCommentTest() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(USER_ID_HEADER, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(USER_ID_HEADER, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusSeconds(2);
        LocalDateTime end = LocalDateTime.now().plusSeconds(6);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        Thread.sleep(10000);
        String commentText = "Comment";
        performRequest(POST, "/items/" + item.getId() + "/comment", createJson(Map.of("text", commentText)), bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentText));
    }

}