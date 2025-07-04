package ru.practicum.shareit.item.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        log.info("Добавлена вещь {}", item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByOwnerId(User user) {
        return items.values().stream().filter(item -> item.getOwner().equals(user)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        return items.values()
                .stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()
                        || item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItemById(Item item) {
        items.put(item.getId(), item);
        log.info("Данные вещи {} обновлены", item);
        return item;
    }

}
