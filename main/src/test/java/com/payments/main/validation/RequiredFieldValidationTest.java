package com.payments.main.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

record RequiredFieldInput(
        String fieldName,
        String otherFieldName
) {
}

public class RequiredFieldValidationTest {
    @Test
    void shouldThrowMissingFieldIfFieldIsMissing() {
        final String field = "fieldName";
        final RequiredFieldValidation sut = new RequiredFieldValidation(field);
        final RequiredFieldInput input = new RequiredFieldInput(null, null);

        assertThrows(ValidationException.MissingField.class, () -> sut.validate(input));
    }

    @Test
    void shouldNotThrowIfValidationSucceeds() {
        final String field = "fieldName";
        final RequiredFieldValidation sut = new RequiredFieldValidation(field);
        final RequiredFieldInput input = new RequiredFieldInput("any value", null);

        assertDoesNotThrow(() -> sut.validate(input));
    }
}
