package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String PATH = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        return post("", userId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getRequestsByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        String path = UriComponentsBuilder.fromPath("/{requestId}")
                .buildAndExpand(requestId)
                .toUriString();
        return get(path, userId);
    }

}