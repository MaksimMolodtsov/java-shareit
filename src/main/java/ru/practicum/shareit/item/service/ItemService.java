package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, ItemCreateDto itemCreateDto);

    Item getItemById(Long itemId);

    List<Item> getItemsByOwnerId(Long userId);

    Item updateItemById(Long userId, Long itemId, ItemDto itemDto);

    List<Item> searchItemsByText(String text);

    Comment createComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);

}