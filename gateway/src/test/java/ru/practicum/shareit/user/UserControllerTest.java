package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BasicControllerTest<UserClient> {

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = UserCreateDto.builder()
                .name("User")
                .email("user@example.com")
                .build();
        userUpdateDto = UserUpdateDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();
    }

    @Test
    void createUserAndGetOk() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<UserCreateDto> captor = ArgumentCaptor.forClass(UserCreateDto.class);
        verify(client).createUser(captor.capture());
        assertEquals(userCreateDto.getName(), captor.getValue().getName());
    }

    @Test
    void createUserAndGetBadRequest() throws Exception {
        UserCreateDto invalidDto = UserCreateDto.builder()
                .name("")
                .email("invalid-email")
                .build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {
        when(client.createUser(any(UserCreateDto.class)))
                .thenThrow(new RuntimeException("Test exception"));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void updateUserAndGetOk() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());
        ArgumentCaptor<UserUpdateDto> captor = ArgumentCaptor.forClass(UserUpdateDto.class);
        verify(client).updateUserById(eq(1L), captor.capture());
        assertEquals(userUpdateDto.getEmail(), captor.getValue().getEmail());
    }

    @Test
    void updateUserForWrongId() throws Exception {
        mockMvc.perform(patch("/users/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
        verify(client).getUserById(eq(1L));
    }

    @Test
    void getUserByIdAndGetBadRequest() throws Exception {
        mockMvc.perform(get("/users/-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(client).deleteUserById(eq(1L));
    }

    @Test
    void deleteUserForWrongId() throws Exception {
        mockMvc.perform(delete("/users/0"))
                .andExpect(status().isInternalServerError());
    }

}