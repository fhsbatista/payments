package com.payments.main.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailValidationTest {
    @Test
    void shouldThrowIfEmailIsInvalid() {
        final EmailValidation sut = new EmailValidation("email");
        final TestInput input = new TestInput("emailemail.com", null);

        assertThrows(ValidationException.InvalidEmail.class, () -> sut.validate(input));

    }
}
