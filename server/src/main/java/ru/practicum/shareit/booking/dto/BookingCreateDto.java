package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}