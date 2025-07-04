package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    User createUser(UserDto userDto);

    User getUserById(Long id);

    void deleteUserById(Long id);

    User updateUserById(Long id, UserDto userDto);

}