package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    User createUser(UserCreateDto userCreateDto);

    User getUserById(Long id);

    void deleteUserById(Long id);

    User updateUserById(Long id, UserUpdateDto userUpdateDto);

    void checkEmail(String email);

}