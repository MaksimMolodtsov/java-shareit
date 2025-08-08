package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public abstract class BasicDtoTest<T> {

    @Autowired
    protected JacksonTester<T> json;

    protected final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    protected <T> void assertValidationFailsWithEmptyFields(T dto, int expectedViolations, Class<?>... annotationTypes) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        assertThat(violations).hasSize(expectedViolations);
        List<Class<?>> actualAnnotationTypes = violations.stream()
                .map(v -> v.getConstraintDescriptor().getAnnotation().annotationType())
                .collect(Collectors.toList());
        assertThat(actualAnnotationTypes).containsExactlyInAnyOrder(annotationTypes);
    }

    protected <T> void assertJsonField(JsonContent<T> json, String fieldPath, Object expectedValue) {
        if (expectedValue == null) {
            assertThat(json).extractingJsonPathValue(fieldPath).isNull();
        } else if (expectedValue instanceof String) {
            assertThat(json).extractingJsonPathStringValue(fieldPath).isEqualTo(expectedValue);
        } else if (expectedValue instanceof Number) {
            assertThat(json).extractingJsonPathNumberValue(fieldPath).isEqualTo(expectedValue);
        } else if (expectedValue instanceof Boolean) {
            assertThat(json).extractingJsonPathBooleanValue(fieldPath).isEqualTo(expectedValue);
        }
    }

    protected <T> void assertViolationHasAnnotation(Set<ConstraintViolation<T>> violations, Class<?> annotationType) {
        assertThat(violations)
                .anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType().equals(annotationType));
    }

    protected <T> void assertNullFieldsValid(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

}