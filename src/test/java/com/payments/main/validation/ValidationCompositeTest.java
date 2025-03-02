package com.payments.main.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ValidationCompositeTest {
    private Validation validation1;
    private Validation validation2;

    @BeforeEach
    void setup() {
        validation1 = mock(Validation.class);
        validation2 = mock(Validation.class);
    }

    List<Validation> validations() {
        return List.of(validation1, validation2);
    }

    ValidationComposite makeSut() {
        return new ValidationComposite(validations());
    }

    @Test
    void shouldReturnErrorIfAnyValidationFails() throws ValidationException {
        final ValidationComposite sut = makeSut();
        final ValidationException exception = new ValidationException();
        doThrow(exception).when(validation1).validate(any());

        ValidationException thrown = assertThrows(exception.getClass(), () -> sut.validate(""));
        assertEquals(exception, thrown);
    }

    @Test
    void shouldReturnFirstErrorIfMoreThanOneValidationFail() throws ValidationException {
        final ValidationComposite sut = makeSut();
        final ValidationException exception1 = new ValidationException();
        final ValidationException exception2 = new ValidationException();
        doThrow(exception1).when(validation1).validate(any());
        doThrow(exception2).when(validation2).validate(any());

        ValidationException thrown = assertThrows(exception1.getClass(), () -> sut.validate(""));
        assertEquals(exception1, thrown);
    }
}
