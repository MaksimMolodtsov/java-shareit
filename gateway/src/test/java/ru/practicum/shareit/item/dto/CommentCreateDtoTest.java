package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BasicDtoTest;

import java.io.IOException;
import java.util.Set;

class CommentCreateDtoTest extends BasicDtoTest<CommentCreateDto> {

    @Test
    void testSerialize() throws IOException {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("Comment")
                .build();
        JsonContent<CommentCreateDto> result = json.write(dto);
        assertJsonField(result, "@.text", "Comment");
    }

    @Test
    void testValidationSuccess() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("Comment")
                .build();
        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFail() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("")
                .build();
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }

    @Test
    void testNullTextValidation() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text(null)
                .build();
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }

}