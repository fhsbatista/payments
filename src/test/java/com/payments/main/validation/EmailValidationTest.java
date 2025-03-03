package com.payments.main.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

record EmailInput(String email) {
}

public class EmailValidationTest {
    @Test
    void shouldThrowIfEmailIsInvalid() {
        final EmailValidation sut = new EmailValidation("email");
        final EmailInput input = new EmailInput("emailemail.com");

        assertThrows(ValidationException.InvalidEmail.class, () -> sut.validate(input));
    }

    @Test
    void shouldThrowNothingIfEmailIsValid() {
        final EmailValidation sut = new EmailValidation("email");
        final EmailInput input = new EmailInput("email@email.com");

        assertDoesNotThrow(() -> sut.validate(input));
    }
}


