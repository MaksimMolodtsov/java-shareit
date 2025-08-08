package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemCreateDto {

    @NotBlank(message = "Имя пользователя отсутствует или содержит пробелы")
    private String name;
    @NotBlank(message = "Описание вещи отсутствует")
    private String description;
    @NotNull(message = "Не указан статус доступности вещи")
    private Boolean available;
    @Min(value = 1, message = "отрицательный ID")
    private Long requestId;

}