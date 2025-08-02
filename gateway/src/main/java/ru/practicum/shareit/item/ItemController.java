package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                             @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.createItem(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return itemClient.getItemsByOwnerId(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                  @PathVariable @Positive Long itemId,
                                  @RequestBody ItemDto itemDto) {
        return itemClient.updateItemById(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text) {
        return itemClient.searchItemsByText(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentCreateDto commentCreateDto) {
        return itemClient.createComment(userId, itemId, commentCreateDto);
    }

}