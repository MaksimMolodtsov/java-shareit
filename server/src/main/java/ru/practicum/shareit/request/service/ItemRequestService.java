package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(Long userId, ItemRequestCreateDto requestDto);

    List<ItemRequest> getRequestsByUserId(Long userId);

    List<ItemRequest> getAllRequestsByUserId(Long userId);

    ItemRequest getRequestById(Long userId, Long requestId);

}