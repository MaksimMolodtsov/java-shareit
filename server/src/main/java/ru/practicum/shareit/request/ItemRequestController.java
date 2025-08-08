package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequest itemRequest = itemRequestService.createItemRequest(userId, itemRequestCreateDto);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<ItemRequest> requestsByUserId = itemRequestService.getRequestsByUserId(userId);
        return requestsByUserId.stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<ItemRequest> allRequestsByUserId = itemRequestService.getAllRequestsByUserId(userId);
        return allRequestsByUserId.stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        ItemRequest itemRequest = itemRequestService.getRequestById(userId, requestId);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

}