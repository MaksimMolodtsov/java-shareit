package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentForItemDto> comments;

}