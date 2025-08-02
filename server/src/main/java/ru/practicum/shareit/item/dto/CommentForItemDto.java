package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentForItemDto {

    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;

}