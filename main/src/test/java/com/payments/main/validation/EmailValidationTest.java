package com.payments.main.validation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

record EmailInput(String email) {
}

public class EmailValidationTest {
    @Test
    void shouldThrowIfEmailIsInvalid() {
        final EmailValidation sut = new EmailValidation("email");
        final List<String> invalidEmails = List.of(
                "usuario@.com",
                "usuario@com",
                "usuario@site..com",
                "usuario@site,com",
                "usuario site@com",
                "@site.com",
                "usuario@site",
                "usuario@site#com",
                "usuáriô@site.com",
                "usuario@site..com.br",
                "usuario@@site.com",
                "usuario@-site.com",
                "usuario@site-.com",
                "usuario@site_com",
                ".usuario@site.com",
                "usuario.@site.com",
                "usuario@site/com",
                "usuario@site*.com"
        );

        invalidEmails.forEach(email -> {
            System.out.println(email);
            assertThrows(
                    ValidationException.InvalidEmail.class,
                    () -> sut.validate(new EmailInput(email)));
        });

    }

    @Test
    void shouldThrowNothingIfEmailIsValid() {
        final EmailValidation sut = new EmailValidation("email");
        final EmailInput input = new EmailInput("email@email.com");

        assertDoesNotThrow(() -> sut.validate(input));
    }
}


