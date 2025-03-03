package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
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
    private GetUserBalanceRepository getUserBalanceRepository;

    @BeforeEach
    void setup() {
        createTransactionRepository = mock(CreateTransactionRepository.class);
        getUserBalanceRepository = mock(GetUserBalanceRepository.class);
        mockSuccess();
    }

    void mockSuccess() {
        final Transaction transaction = new Transaction(
                123L,
                124L,
                BigDecimal.valueOf(2300.0),
                Instant.now()
        );
        when(createTransactionRepository.create(any()))
                .thenReturn(Optional.of(transaction));
    }

    DbCreateTransaction makeSut() {
        return new DbCreateTransaction(
                createTransactionRepository,
                getUserBalanceRepository
        );
    }

    CreateTransactionInput makeInput() {
        return new CreateTransactionInput(
                123L,
                123L,
                BigDecimal.valueOf(100.0)
        );
    }

    CreateTransactionInput makeInput(BigDecimal amount) {
        return new CreateTransactionInput(
                123L,
                123L,
                amount
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

    @Test
    void shouldReturnTransactionOnRepositorySuccess() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();
        final Transaction transaction = mock(Transaction.class);
        when(createTransactionRepository.create(input)).thenReturn(Optional.of(transaction));

        final Transaction result = sut.call(input);

        assertEquals(transaction, result);
    }

    @Test
    void shouldCallGetBalanceRepositoryWithCorrectId() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.call(input);

        verify(getUserBalanceRepository).getUserBalance(input.payerId());
    }

    @Test
    void shouldThrowIfBalanceIsNotEnough() {
        final DbCreateTransaction sut = makeSut();
        final BigDecimal balance = BigDecimal.valueOf(1.0);
        final CreateTransactionInput input = makeInput(balance.add(BigDecimal.valueOf(5.0)));

        when(getUserBalanceRepository.getUserBalance(any())).thenReturn(Optional.of(balance));

        assertThrows(CustomExceptions.InsufficientFunds.class, () -> sut.call(input));
    }


}
