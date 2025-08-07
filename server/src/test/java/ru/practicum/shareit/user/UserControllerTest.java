package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.BasicControllerTest;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class UserControllerTest extends BasicControllerTest {

    @Test
    void createUserTest() throws Exception {
        User user = createUser();
        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUserWithExistingEmailTest() throws Exception {
        User user = createUser();
        String userJson = createJson(Map.of(
                "name", "Another User",
                "email", user.getEmail()
        ));
        performRequest(POST, "/users", userJson)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void updateUserTest() throws Exception {
        User user = createUser();
        String updatedUserJson = createJson(Map.of(
                "name", "Updated Name",
                "email", "updated@example.com"
        ));
        performRequest(PATCH, "/users/" + user.getId(), updatedUserJson)
                .andExpect(status().isOk());
        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void getNonExistentUserByIdTest() throws Exception {
        performRequest(GET, "/users/999")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getUserByIdTest() throws Exception {
        User user = createUser();
        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        User user = createUser();
        performRequest(DELETE, "/users/" + user.getId())
                .andExpect(status().isOk());
        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentUser() throws Exception {
        performRequest(DELETE, "/users/999")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

}