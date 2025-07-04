package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank(message = "Имя пользователя отсутствует или содержит пробелы")
    private String name;
    @NotBlank(message = "Описание вещи отсутствует")
    private String description;
    @NotNull(message = "Не указан статус доступности вещи")
    private Boolean available;
    private User owner;
    private ItemRequest request;

}
