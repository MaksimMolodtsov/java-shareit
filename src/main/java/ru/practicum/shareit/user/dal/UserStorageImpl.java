package ru.practicum.shareit.user.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public void checkEmail(String email) {
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                throw new ExistException("Указан существующий email: " + email);
            }
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
        log.info("Удален пользователь с id: {}", id);
    }

    @Override
    public User updateUserById(User user) {
        users.put(user.getId(), user);
        log.info("Данные пользователя {} обновлены", user);
        return user;
    }

}
