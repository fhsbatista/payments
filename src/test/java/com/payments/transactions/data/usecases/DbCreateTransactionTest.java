package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DbCreateTransactionTest {
    private CreateTransactionRepository createTransactionRepository;

    @BeforeEach
    void setup() {
        createTransactionRepository = mock(CreateTransactionRepository.class);
        mockSuccess();
    }

    void mockSuccess() {
        final Transaction transaction = new Transaction(
                123L,
                124L,
                BigDecimal.valueOf(2300.0),
                Instant.now()
        );
        when(createTransactionRepository.create(makeInput())).thenReturn(Optional.of(transaction));
    }

    DbCreateTransaction makeSut() {
        return new DbCreateTransaction(createTransactionRepository);
    }

    CreateTransactionInput makeInput() {
        return new CreateTransactionInput(
                123L,
                123L,
                BigDecimal.valueOf(100.0)
        );
    }

    @Test
    void shouldCallRepositoryWithCorrectValues() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.call(input);

        verify(createTransactionRepository).create(input);
    }

    @Test
    void shouldThrowIfRepositoryThrows() {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();
        final Exception exception = new RuntimeException("test exception");
        when(createTransactionRepository.create(input)).thenThrow(exception);

        Exception thrown = assertThrows(Exception.class, () -> sut.call(input));
        assertEquals(exception, thrown);
    }
}
