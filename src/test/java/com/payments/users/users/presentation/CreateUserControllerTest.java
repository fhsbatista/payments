package com.payments.users.users.presentation;

import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
import com.payments.users.presentation.CreateUserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateUserControllerTest {
    private CreateUser usecase;

    CreateUserController makeSut() {
        return new CreateUserController(usecase);
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
    void setup() {
        usecase = mock(CreateUser.class);
    }

    @Test
    void shouldCallCreateUserUsecaseWithCorrectValues() throws CustomExceptions {
        final CreateUserController sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.handle(input);

        verify(usecase).call(input);
    }
}
