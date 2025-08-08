package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"item", "user"})
public class Comment {

    private Long id;
    private String text;
    private Item item;
    private User user;
    private LocalDateTime createdAt;

}
