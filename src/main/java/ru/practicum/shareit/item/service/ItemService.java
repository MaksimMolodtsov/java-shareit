package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, ItemDto itemDto);
    Item getItemById(Long itemId);
    List<Item> getItemsByOwnerId(Long userId);
    List<Item> searchItemsByText(String text);
    Item updateItemById(Long userId, Long itemId, ItemDto itemDto);

}
