package com.payments.users.users.presentation;

import com.payments.main.validation.Validation;
import com.payments.main.validation.ValidationException;
import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
import com.payments.users.presentation.ErrorPresenter;
import com.payments.users.presentation.UserPresenter;
import com.payments.users.presentation.UsersController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CreateUserControllerTest {
    private Validation validation;
    private CreateUser usecase;

    UsersController makeSut() {
        return new UsersController(validation, usecase);
    }

    CreateUserInput makeInput() {
        return new CreateUserInput(
                "John Doe",
                "1234567800",
                "john@doe.com",
                "123!@#"
        );
    }

    @BeforeEach
    void setup() throws Exception {
        validation = mock(Validation.class);
        usecase = mock(CreateUser.class);
        mockSuccess();
    }

    void mockSuccess() throws Exception {
        final User user = new User(
                123L,
                "John Doe",
                "1234567800",
                "john@doe.com"
        );
        when(usecase.call(any())).thenReturn(user);
    }

    void mockFailure(CustomExceptions exception) throws Exception {
        when(usecase.call(any())).thenThrow(exception);
    }

    @Test
    void shouldCallCreateUserUsecaseWithCorrectValues() throws Exception {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.handle(input);

        verify(usecase).call(input);
    }

    @Test
    void shouldReturn201WithUserOnUsecaseSuccess() throws CustomExceptions {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();

        final ResponseEntity<?> response = sut.handle(input);
        final UserPresenter user = (UserPresenter) response.getBody();

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(user);
        assertEquals(input.name(), user.name());
        assertEquals(input.cpf(), user.cpf());
        assertEquals(input.email(), user.email());
    }

    @Test
    void shouldReturn400WithCorrectMessageOnUsecaseCustomException() throws Exception {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();
        final CustomExceptions exception = new CustomExceptions.EmailAlreadyRegistered();
        final String exceptionMessage = ErrorPresenter.DICTIONARY.get(exception.getClass());
        mockFailure(exception);

        final ResponseEntity<?> response = sut.handle(input);
        final var responseBody = (ErrorPresenter) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(responseBody);
        assertEquals(exceptionMessage, responseBody.message());
    }

    @Test
    void shouldReturn500OnUsecaseNotKnownException() throws Exception {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();
        when(usecase.call(input)).thenThrow(new Exception());

        final ResponseEntity<?> response = sut.handle(input);
        final var responseBody = (ErrorPresenter) response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(responseBody);
        assertNotNull("Internal server error", responseBody.message());
    }

    @Test
    void shouldCallValidationWithCorrectValues() throws ValidationException {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.handle(input);

        verify(validation).validate(input);
    }
}
