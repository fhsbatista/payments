package com.payments.users.users.presentation;

import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
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
    private CreateUser usecase;

    UsersController makeSut() {
        return new UsersController(usecase);
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
    void setup() throws CustomExceptions {
        usecase = mock(CreateUser.class);
        mockSuccess();
    }

    void mockSuccess() throws CustomExceptions {
        final User user = new User(
                123L,
                "John Doe",
                "1234567800",
                "john@doe.com"
        );
        when(usecase.call(any())).thenReturn(user);
    }

    @Test
    void shouldCallCreateUserUsecaseWithCorrectValues() throws CustomExceptions {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.handle(input);

        verify(usecase).call(input);
    }

    @Test
    void shouldReturn201WithUserOnUsecaseSuccess() throws CustomExceptions {
        final UsersController sut = makeSut();
        final CreateUserInput input = makeInput();

        final ResponseEntity<UserPresenter> response = sut.handle(input);
        final UserPresenter user = response.getBody();

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(user);
        assertEquals(input.name(), user.name());
        assertEquals(input.cpf(), user.cpf());
        assertEquals(input.email(), user.email());
    }
}
