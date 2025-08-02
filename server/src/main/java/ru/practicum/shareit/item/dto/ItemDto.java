package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    @JsonIdentityReference(alwaysAsId = true)
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentForItemDto> comments;

}