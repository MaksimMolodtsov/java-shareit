package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    void checkEmail(String email);

    Optional<User> getUserById(Long id);

    void deleteUserById(Long id);

    User updateUserById(User user);

}