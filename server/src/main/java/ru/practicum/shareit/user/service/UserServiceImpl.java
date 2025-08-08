package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User createUser(UserCreateDto userCreateDto) {
        this.checkEmail(userCreateDto.getEmail());
        User user = userMapper.toUser(userCreateDto);
        log.info("Создан пользователь: {}", user);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id: {} не найден", id));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
        log.info("Удален пользователь с id: {}", id);
    }

    @Override
    @Transactional
    public User updateUserById(Long id, UserUpdateDto userUpdateDto) {
        User user = getUserById(id);
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            this.checkEmail(userUpdateDto.getEmail());
            user.setEmail(userUpdateDto.getEmail());
        }
        log.info("Обновлен пользователь: {}", user);
        return userRepository.save(user);
    }

    @Override
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ExistException("Указан существующий email: " + email);
        }
    }

}