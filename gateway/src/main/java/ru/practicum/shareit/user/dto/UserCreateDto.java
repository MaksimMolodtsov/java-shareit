package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreateDto {

    @NotBlank(message = "Имя пользователя отсутствует или содержит пробелы")
    private String name;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Неверный формат email")
    private String email;

}