package ru.practicum.shareit.item.dal;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface ItemStorage {

    Item createItem(Item item);
    Optional<Item> getItemById(Long itemId);
    List<Item> getItemsByOwnerId(User user);
    List<Item> searchItemsByText(String text);
    Item updateItemById(Item item);

}
