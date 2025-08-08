package ru.practicum.shareit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.HttpMethodEnum;
import ru.practicum.shareit.utils.RandomUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.POST;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BasicControllerTest {

    @Autowired
    protected WebApplicationContext context;
    protected MockMvc mockMvc;
    protected final ObjectMapper objectMapper;
    protected static final String USER_ID_HEADER = "X-Sharer-User-Id";

    protected BasicControllerTest() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    protected String createJson(Map<String, Object> fields) {
        try {
            return objectMapper.writeValueAsString(fields);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании JSON", e);
        }
    }

    protected Map<String, Object> itemDtoToMap(ItemDto itemDto) {
        return Map.of(
                "name", itemDto.getName(),
                "description", itemDto.getDescription(),
                "available", itemDto.getAvailable()
        );
    }

    protected Map<String, Object> userDtoToMap(UserDto userDto) {
        return Map.of(
                "name", userDto.getName(),
                "email", userDto.getEmail()
        );
    }

    protected Map<String, Object> bookingDtoToMap(BookingDto bookingDto) {
        return Map.of(
                "itemId", bookingDto.getItem().getId(),
                "start", bookingDto.getStart().toString(),
                "end", bookingDto.getEnd().toString()
        );
    }

    protected Map<String, Object> itemRequestDtoToMap(ItemRequestCreateDto requestDto) {
        return Map.of(
                "description", requestDto.getDescription()
        );
    }

    protected Map<String, Object> commentDtoToMap(CommentDto commentDto) {
        return Map.of(
                "text", commentDto.getText()
        );
    }

    protected MultiValueMap<String, String> createHeaders(String key, String value) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(key, value);
        return headers;
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url,
            String json,
            MultiValueMap<String, String> params,
            MultiValueMap<String, String> headers,
            Object... uriVars
    ) throws Exception {
        if (params != null && !params.isEmpty()) {
            url = UriComponentsBuilder.fromPath(url).queryParams(params).build().toUriString();
        }
        MockHttpServletRequestBuilder requestBuilder = switch (method) {
            case POST -> post(url, uriVars);
            case GET -> get(url, uriVars);
            case PUT -> put(url, uriVars);
            case PATCH -> patch(url, uriVars);
            case DELETE -> delete(url, uriVars);
        };
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        if (headers != null && !headers.isEmpty()) {
            requestBuilder.headers(new HttpHeaders(headers));
        }
        if (json != null && !json.isEmpty()) {
            requestBuilder.content(json);
        }
        return mockMvc.perform(requestBuilder);
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url,
            MultiValueMap<String, String> params,
            MultiValueMap<String, String> headers,
            Object... uriVars
    ) throws Exception {
        return performRequest(method, url, "", params, headers, uriVars);
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url,
            String json,
            MultiValueMap<String, String> headers,
            Object... uriVars
    ) throws Exception {
        return performRequest(method, url, json, new LinkedMultiValueMap<>(), headers, uriVars);
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url,
            MultiValueMap<String, String> headers,
            Object... uriVars
    ) throws Exception {
        return performRequest(method, url, "", new LinkedMultiValueMap<>(), headers, uriVars);
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url,
            String json
    ) throws Exception {
        return performRequest(method, url, json, null, null, new Object[0]);
    }

    protected ResultActions performRequest(
            HttpMethodEnum method,
            String url
    ) throws Exception {
        return performRequest(method, url, "", null, null, new Object[0]);
    }

    protected User createUser() throws Exception {
        UserDto userDto = RandomUtils.getRandomUser();
        String jsonUserDto = createJson(userDtoToMap(userDto));

        String response = performRequest(POST, "/users", jsonUserDto)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);

        return User.builder()
                .id(jsonNode.get("id").asLong())
                .name(jsonNode.get("name").asText())
                .email(jsonNode.get("email").asText())
                .build();
    }

    protected Item createItem(MultiValueMap<String, String> headers, Boolean available) throws Exception {
        ItemDto itemDto = RandomUtils.getRandomItem(available);
        String jsonItemDto = createJson(itemDtoToMap(itemDto));
        String response = performRequest(POST, "/items", jsonItemDto, headers)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return Item.builder()
                .id(jsonNode.get("id").asLong())
                .name(jsonNode.get("name").asText())
                .description(jsonNode.get("description").asText())
                .available(jsonNode.get("available").asBoolean())
                .build();
    }


    protected Booking createBooking(MultiValueMap<String, String> headers,
                                    Long itemId,
                                    LocalDateTime start,
                                    LocalDateTime end) throws Exception {
        BookingDto bookingDto = RandomUtils.getBooking(itemId, start, end);
        String jsonBookingDto = createJson(bookingDtoToMap(bookingDto));

        String response = performRequest(POST, "/bookings", jsonBookingDto, headers)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return Booking.builder()
                .id(jsonNode.get("id").asLong())
                .start(LocalDateTime.parse(jsonNode.get("start").asText()))
                .end(LocalDateTime.parse(jsonNode.get("end").asText()))
                .status(BookingStatus.valueOf(jsonNode.get("status").asText()))
                .build();
    }

}