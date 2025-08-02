package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreateDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

}