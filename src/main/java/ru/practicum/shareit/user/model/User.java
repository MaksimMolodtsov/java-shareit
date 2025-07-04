package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private Long id;
    //@NotBlank(message = "Имя пользователя отсутствует или содержит пробелы")
    private String name;
    //@Email(message = "Неверный формат email")
    //@NotBlank(message = "Неверный формат email")
    private String email;

}
