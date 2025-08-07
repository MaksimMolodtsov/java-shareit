package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BasicDtoTest;

import java.io.IOException;
import java.util.Set;

class ItemRequestCreateDtoTest extends BasicDtoTest<ItemRequestCreateDto> {

    @Test
    void testSerialize() throws IOException {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("Description")
                .build();
        JsonContent<ItemRequestCreateDto> result = json.write(dto);
        assertJsonField(result, "@.description", "Description");
    }

    @Test
    void testValidationIsOk() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("Valid description")
                .build();
        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationIsFail() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("")
                .build();
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }

    @Test
    void testNullDescription() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description(null)
                .build();
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }

}