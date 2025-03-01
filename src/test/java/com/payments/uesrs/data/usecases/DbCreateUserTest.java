package com.payments.uesrs.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DbCreateUserTest {
    private CreateUserRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(CreateUserRepository.class);
    }

    @Test
    void shouldCallCreateAccountRepositoryWithCorrectValues() {
        final DbCreateUser sut = new DbCreateUser(repository);
        final CreateUserInput input = new CreateUserInput(
                "John Doe",
                "1234567800",
                "john@doe.com",
                "123!@#"
        );
        sut.call(input);
        verify(repository).call(input);
    }
}
