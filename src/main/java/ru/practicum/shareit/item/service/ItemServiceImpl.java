package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private Long nextId = 1L;

    @Override
    public Item createItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId++);
        item.setOwner(userService.getUserById(userId));
        log.info("Создана вещь: {}", item);
        return itemStorage.createItem(item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с указанным id: {} не найдена", itemId));
    }

    @Override
    public List<Item> getItemsByOwnerId(Long userId) {
        User user = userService.getUserById(userId);
        return itemStorage.getItemsByOwnerId(user);
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemStorage.searchItemsByText(text);
    }

    @Override
    public Item updateItemById(Long userId, Long itemId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        if (item.getOwner().equals(user)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
        }
        return itemStorage.updateItemById(item);
    }


}
