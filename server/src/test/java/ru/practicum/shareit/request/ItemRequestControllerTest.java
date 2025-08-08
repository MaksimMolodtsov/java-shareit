package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RandomUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.GET;
import static ru.practicum.shareit.utils.HttpMethodEnum.POST;

class ItemRequestControllerTest extends BasicControllerTest {

    @Test
    void createItemTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        ItemRequestCreateDto requestDto = RandomUtils.getRandomItemRequest();
        String json = createJson(itemRequestDtoToMap(requestDto));
        performRequest(POST, "/requests", json, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        ItemRequestCreateDto requestDto = RandomUtils.getRandomItemRequest();
        String json = createJson(itemRequestDtoToMap(requestDto));
        performRequest(POST, "/requests", json, headers);
        performRequest(GET, "/requests", headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        User user1 = createUser();
        MultiValueMap<String, String> user1Headers = createHeaders(USER_ID_HEADER, user1.getId().toString());
        User user2 = createUser();
        MultiValueMap<String, String> user2Headers = createHeaders(USER_ID_HEADER, user2.getId().toString());
        ItemRequestCreateDto requestDto = RandomUtils.getRandomItemRequest();
        String json = createJson(itemRequestDtoToMap(requestDto));
        performRequest(POST, "/requests", json, user1Headers);
        performRequest(GET, "/requests/all", user2Headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        ItemRequestCreateDto requestDto = RandomUtils.getRandomItemRequest();
        String json = createJson(itemRequestDtoToMap(requestDto));
        String response = performRequest(POST, "/requests", json, headers)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long requestId = objectMapper.readTree(response).get("id").asLong();
        performRequest(GET, "/requests/" + requestId, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getRequestWithWrongIdFailTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(USER_ID_HEADER, user.getId().toString());
        performRequest(GET, "/requests/999", headers)
                .andExpect(status().isNotFound());
    }

}