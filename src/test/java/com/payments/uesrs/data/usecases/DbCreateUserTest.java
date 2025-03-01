package com.payments.uesrs.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DbCreateUserTest {
    private CreateUserRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(CreateUserRepository.class);
    }

    DbCreateUser makeSut() {
        return new DbCreateUser(repository);
    }

    CreateUserInput makeInput() {
        return new CreateUserInput(
                "John Doe",
                "1234567800",
                "john@doe.com",
                "123!@#"
        );
    }

    @Test
    void shouldCallRepositoryWithCorrectValues() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        sut.call(input);
        verify(repository).call(input);
    }

    @Test
    void shouldThrowIfRepositoryThrows() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final Exception exception = new RuntimeException("test exception");
        when(repository.call(input)).thenThrow(exception);

        Exception thrown = assertThrows(Exception.class, () -> sut.call(input));
        assertEquals(exception, thrown);
    }

    @Test
    void shouldReturnUserOnRepositorySuccess() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final User user = mock(User.class);
        when(repository.call(input)).thenReturn(user);

        final User result = sut.call(input);
        assertEquals(user, result);
    }
}
