package ru.practicum.shareit.request.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {

    private String description;

}