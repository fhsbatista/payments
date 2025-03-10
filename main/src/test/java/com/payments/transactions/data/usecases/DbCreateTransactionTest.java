package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import com.payments.transactions.domain.usecases.SendNotification;
import com.payments.transactions.domain.usecases.SendNotificationInput;
import com.payments.users.data.repositories.GetUserByIdRepository;
import com.payments.users.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DbCreateTransactionTest {
    private Authorizer authorizer;
    private CreateTransactionRepository createTransactionRepository;
    private GetUserBalanceRepository getUserBalanceRepository;
    private SendNotification sendNotification;
    private GetUserByIdRepository getUserByIdRepository;

    @BeforeEach
    void setup() {
        authorizer = mock(Authorizer.class);
        createTransactionRepository = mock(CreateTransactionRepository.class);
        getUserBalanceRepository = mock(GetUserBalanceRepository.class);
        sendNotification = mock(SendNotification.class);
        getUserByIdRepository = mock(GetUserByIdRepository.class);
        mockSuccess();
    }

    void mockSuccess() {
        final Transaction transaction = new Transaction(
                1L,
                123L,
                124L,
                BigDecimal.valueOf(2300.0),
                Instant.now()
        );
        final User user = makeUser();
        when(getUserByIdRepository.getById(any())).thenReturn(Optional.of(user));
        when(getUserBalanceRepository.getUserBalance(any()))
                .thenReturn(Optional.of(BigDecimal.valueOf(10000.0)));
        when(createTransactionRepository.create(any()))
                .thenReturn(Optional.of(transaction));
        when(authorizer.isAuthorized(any())).thenReturn(true);
    }

    DbCreateTransaction makeSut() {
        return new DbCreateTransaction(
                authorizer,
                createTransactionRepository,
                getUserBalanceRepository,
                sendNotification,
                getUserByIdRepository
        );
    }

    CreateTransactionInput makeInput(User payer, User payee) {
        return new CreateTransactionInput(
                payer.id(),
                payee.id(),
                BigDecimal.valueOf(100.0)
        );
    }

    CreateTransactionInput makeInput() {
        return new CreateTransactionInput(
                makeUser().id(),
                makeUser().id(),
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

    User makeUser() {
        return new User(
                new Random().nextLong(),
                "user test",
                "1234567800",
                "user@test.com"
        );
    }

    @Test
    void shouldCallGetUserByIdRepositoryForBothPayerAndPayeeIds() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final User payer = makeUser();
        final User payee = makeUser();
        final CreateTransactionInput input = makeInput(payer, payee);
        when(getUserByIdRepository.getById(payer.id())).thenReturn(Optional.of(payer));
        when(getUserByIdRepository.getById(payee.id())).thenReturn(Optional.of(payee));

        sut.call(input);

        verify(getUserByIdRepository).getById(payer.id());
        verify(getUserByIdRepository).getById(payee.id());
    }

    @Test
    void shouldThrowIfGetUserByIdRepositoryCannotFindPayerId() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final User payer = makeUser();
        final User payee = makeUser();
        final CreateTransactionInput input = makeInput(payer, payee);
        when(getUserByIdRepository.getById(payer.id())).thenReturn(Optional.empty());
        when(getUserByIdRepository.getById(payee.id())).thenReturn(Optional.of(payee));

        assertThrows(CustomExceptions.PayerNotFound.class, () -> sut.call(input)) ;
    }

    @Test
    void shouldThrowIfGetUserByIdRepositoryCannotFindPayeeId() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final User payer = makeUser();
        final User payee = makeUser();
        final CreateTransactionInput input = makeInput(payer, payee);
        when(getUserByIdRepository.getById(payer.id())).thenReturn(Optional.of(payer));
        when(getUserByIdRepository.getById(payee.id())).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.PayeeNotFound.class, () -> sut.call(input)) ;
    }

    @Test
    void shouldCallAuthorizerWithCorrectValues() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.call(input);

        verify(authorizer).isAuthorized(input);
    }

    @Test
    void shouldThrowIfAuthorizerReturnsFalse() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();
        when(authorizer.isAuthorized(any())).thenReturn(false);

        assertThrows(CustomExceptions.NotAuthorized.class, () -> sut.call(input));

        verify(authorizer).isAuthorized(input);
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

    @Test
    void shouldThrowIfGetUserBalanceRepositoryNotReturnBalance() {
        final DbCreateTransaction sut = makeSut();
        final CreateTransactionInput input = makeInput();
        when(getUserBalanceRepository.getUserBalance(any())).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.UnknownBalance.class, () -> sut.call(input));
    }

    @Test
    void shouldCallSendNotificationCorrectly() throws CustomExceptions {
        final DbCreateTransaction sut = makeSut();
        final User payer = makeUser();
        final User payee = makeUser();
        final CreateTransactionInput input = makeInput(payer, payee);
        when(getUserByIdRepository.getById(payer.id())).thenReturn(Optional.of(payer));
        when(getUserByIdRepository.getById(payee.id())).thenReturn(Optional.of(payee));

        sut.call(input);

        final var payerNotification = new SendNotificationInput(payer.email(), "Your transaction has been cleared");
        final var payeeNotification = new SendNotificationInput(payee.email(), "You have received a transaction");
        verify(sendNotification).send(payerNotification);
        verify(sendNotification).send(payeeNotification);
    }
}
