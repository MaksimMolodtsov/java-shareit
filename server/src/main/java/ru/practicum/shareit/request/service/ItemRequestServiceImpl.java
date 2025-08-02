package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequest createItemRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto);
        itemRequest.setUser(user);
        log.info("Создан запрос на вещь: {}", itemRequest);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getRequestsByUserId(Long userId) {
        userService.getUserById(userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<ItemRequest> getAllRequestsByUserId(Long userId) {
        userService.getUserById(userId);
        return itemRequestRepository.findAllOtherUsersRequests(userId);
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        return itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с указанным id: {} не найден", requestId));
    }

}