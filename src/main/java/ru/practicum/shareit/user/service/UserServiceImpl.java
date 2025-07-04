package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private Long nextId = 1L;

    @Override
    public User createUser(UserDto userDto) {
        userStorage.checkEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        user.setId(nextId++);
        log.info("Создан пользователь: {}", user);
        return userStorage.createUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id: {} не найден", id));
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userStorage.deleteUserById(id);
    }

    @Override
    public User updateUserById(Long id, UserDto userDto) {
        User user = getUserById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userStorage.checkEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        return userStorage.updateUserById(user);
    }
}
