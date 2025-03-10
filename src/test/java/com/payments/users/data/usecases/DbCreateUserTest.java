package com.payments.users.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByCpfRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DbCreateUserTest {
    private CreateUserRepository createUserRepository;
    private GetUserByEmailRepository getUserByEmailRepository;
    private GetUserByCpfRepository getUserByCpfRepository;

    @BeforeEach
    void setup() {
        createUserRepository = mock(CreateUserRepository.class);
        getUserByEmailRepository = mock(GetUserByEmailRepository.class);
        getUserByCpfRepository = mock(GetUserByCpfRepository.class);
        mockSuccess();
    }

    void mockSuccess() {
        final User user = new User(
                123L,
                "John Doe",
                "1234567800",
                "john@doe.com"
        );
        when(createUserRepository.create(makeInput())).thenReturn(Optional.of(user));
    }

    DbCreateUser makeSut() {
        return new DbCreateUser(
                createUserRepository,
                getUserByEmailRepository,
                getUserByCpfRepository
        );
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
    void shouldCallRepositoryWithCorrectValues() throws CustomExceptions {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        sut.call(input);
        verify(createUserRepository).create(input);
    }

    @Test
    void shouldThrowIfRepositoryThrows() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final Exception exception = new RuntimeException("test exception");
        when(createUserRepository.create(input)).thenThrow(exception);

        Exception thrown = assertThrows(Exception.class, () -> sut.call(input));
        assertEquals(exception, thrown);
    }

    @Test
    void shouldReturnUserOnRepositorySuccess() throws CustomExceptions {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final User user = mock(User.class);
        when(createUserRepository.create(input)).thenReturn(Optional.of(user));

        final User result = sut.call(input);
        assertEquals(user, result);
    }

    @Test
    void shouldCallGetUserByEmailRepositoryWithCorrectEmail() throws CustomExceptions {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.call(input);
        verify(getUserByEmailRepository).getByEmail(input.email());
    }

    @Test
    void shouldThrowIfGetUserByEmailRepositoryNotReturnNull() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final User alreadyRegisteredUser = mock(User.class);
        when(getUserByEmailRepository.getByEmail(input.email()))
                .thenReturn(Optional.ofNullable(alreadyRegisteredUser));

        assertThrows(CustomExceptions.EmailAlreadyRegistered.class, () -> sut.call(input));
    }

    @Test
    void shouldCallGetUserByCpfRepositoryWithCorrectCpf() throws CustomExceptions {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.call(input);
        verify(getUserByCpfRepository).getByCpf(input.cpf());
    }

    @Test
    void shouldThrowIfGetUserByCpfRepositoryNotReturnNull() {
        final DbCreateUser sut = makeSut();
        final CreateUserInput input = makeInput();
        final User alreadyRegisteredUser = mock(User.class);
        when(getUserByCpfRepository.getByCpf(input.cpf()))
                .thenReturn(Optional.ofNullable(alreadyRegisteredUser));

        assertThrows(CustomExceptions.CpfAlreadyRegistered.class, () -> sut.call(input));
    }
}
