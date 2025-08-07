package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest extends BasicControllerTest<ItemClient> {

    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();
        commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();
    }

    @Test
    void createItemAndStatusOk() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<ItemCreateDto> captor = ArgumentCaptor.forClass(ItemCreateDto.class);
        verify(client).createItem(eq(1L), captor.capture());
        assertEquals(itemCreateDto.getName(), captor.getValue().getName());
    }

    @Test
    void createItemForMissingHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createItemForInvalidData() throws Exception {
        ItemCreateDto invalidDto = ItemCreateDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItemAndStatusOk() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
        verify(client).updateItemById(eq(1L), eq(1L), any(ItemDto.class));
    }

    @Test
    void updateItemForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemByIdAndStatusOk() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk());
        verify(client).getItemById(eq(1L));
    }

    @Test
    void getItemsByOwnerIdAndStatusOk() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(client).getItemsByOwnerId(eq(1L));
    }

    @Test
    void searchItemsByTextAndStatusOk() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "query"))
                .andExpect(status().isOk());
        verify(client).searchItemsByText(eq("query"));
    }

    @Test
    void createCommentAndStatusOk() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<CommentCreateDto> captor = ArgumentCaptor.forClass(CommentCreateDto.class);
        verify(client).createComment(eq(1L), eq(1L), captor.capture());
        assertEquals(commentCreateDto.getText(), captor.getValue().getText());
    }

    @Test
    void createCommentForMissingHeader() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByOwnerForHeaderUserIdIsZero() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {
        when(client.createItem(anyLong(), any(ItemCreateDto.class)))
                .thenThrow(new RuntimeException());
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isInternalServerError());
    }

}