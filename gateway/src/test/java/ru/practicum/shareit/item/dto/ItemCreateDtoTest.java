package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BasicDtoTest;

import java.io.IOException;
import java.util.Set;

class ItemCreateDtoTest extends BasicDtoTest<ItemCreateDto> {

    @Test
    void testSerialize() throws IOException {

        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        JsonContent<ItemCreateDto> result = json.write(dto);
        assertJsonField(result, "@.name", "Item");
        assertJsonField(result, "@.description", "Description");
        assertJsonField(result, "@.available", true);
    }

    @Test
    void testValidationSuccess() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFail() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();
        assertValidationFailsWithEmptyFields(dto, 3, NotBlank.class, NotBlank.class, NotNull.class);
    }

    @Test
    void testRequestIdValidation() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(0L)
                .build();
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, Min.class);
    }

}