package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.service.ItemRequestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest extends BasicControllerTest<ItemRequestClient> {

    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Description")
                .build();
    }

    @Test
    void createItemAndStatusOk() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<ItemRequestCreateDto> captor = ArgumentCaptor.forClass(ItemRequestCreateDto.class);
        verify(client).createItemRequest(eq(1L), captor.capture());
        assertEquals(itemRequestCreateDto.getDescription(), captor.getValue().getDescription());
    }

    @Test
    void createItemRequestForMissingHeader() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemRequestForInvalidData() throws Exception {
        ItemRequestCreateDto invalidDto = ItemRequestCreateDto.builder()
                .description("")
                .build();
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createItemRequestForWrongHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserRequestsAndStatusOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(client).getRequestsByUserId(eq(1L));
    }

    @Test
    void getUserRequestsForMissingHeader() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById() throws Exception {
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(client).getRequestById(eq(1L), eq(1L));
    }

    @Test
    void getRequestByIdForRequestIdIsNegative() throws Exception {
        mockMvc.perform(get("/requests/-1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {
        when(client.createItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenThrow(new RuntimeException());
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isInternalServerError());
    }

}