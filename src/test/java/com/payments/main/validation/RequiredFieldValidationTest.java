package com.payments.main.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequiredFieldValidationTest {
    @Test
    void shouldThrowMissingFieldIfFieldIsMissing() {
        final String field = "fieldName";
        final RequiredFieldValidation sut = new RequiredFieldValidation(field);
        final TestInput input = new TestInput(null, null);

        assertThrows(ValidationException.MissingField.class, () -> sut.validate(input));
    }

    @Test
    void shouldNotThrowIfValidationSucceeds() {
        final String field = "fieldName";
        final RequiredFieldValidation sut = new RequiredFieldValidation(field);
        final TestInput input = new TestInput("any value", null);

        assertDoesNotThrow(() -> sut.validate(input));
    }
}
