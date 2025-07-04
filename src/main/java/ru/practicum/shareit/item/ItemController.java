package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        Item item = itemService.createItem(userId, itemDto);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        Item item = itemService.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> itemsByOwnerId = itemService.getItemsByOwnerId(userId);
        return itemsByOwnerId.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        List<Item> itemsByText = itemService.searchItemsByText(text);
        return itemsByText.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody ItemDto itemDto) {
        Item item = itemService.updateItemById(userId, itemId, itemDto);
        return ItemMapper.toItemDto(item);
    }

}
