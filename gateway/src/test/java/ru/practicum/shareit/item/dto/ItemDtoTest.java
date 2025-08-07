package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BasicDtoTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;

import java.io.IOException;
import java.util.List;

class ItemDtoTest extends BasicDtoTest<ItemDto> {

    @Test
    void testSerialize() throws IOException {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        JsonContent<ItemDto> result = json.write(dto);
        assertJsonField(result, "@.name", "Item");
        assertJsonField(result, "@.description", "Description");
        assertJsonField(result, "@.available", true);
    }

    @Test
    void testValidationSuccess() {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFail() {
        ItemDto dto = ItemDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();
        assertValidationFailsWithEmptyFields(dto, 3, NotBlank.class, NotBlank.class, NotNull.class);
    }

    @Test
    void testWithAllFields() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(new User())
                .request(new ItemRequest())
                .lastBooking(new Booking())
                .nextBooking(new Booking())
                .comments(List.of(new CommentForItemDto()))
                .build();
        assertNullFieldsValid(dto);
    }

}