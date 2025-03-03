package com.payments.transactions.presentation;

import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionsControllerTest {
    private CreateTransaction usecase;

    TransactionsController makeSut() {
        return new TransactionsController(usecase);
    }

    CreateTransactionInput makeInput() {
        return new CreateTransactionInput(
                1L,
                2L,
                BigDecimal.valueOf(100.0)
        );
    }

    Transaction makeTransaction() {
        return new Transaction(
                1L,
                1L,
                2L,
                BigDecimal.valueOf(100.0),
                Instant.now()
        );
    }

    @BeforeEach
    void setup() throws CustomExceptions {
        usecase = mock(CreateTransaction.class);
        mockSuccess();
    }

    void mockSuccess() throws CustomExceptions {
        when(usecase.call(any())).thenReturn(makeTransaction());
    }

    void mockFailure(CustomExceptions exception) throws Exception {
        when(usecase.call(any())).thenThrow(exception);
    }

    @Test
    void shouldCallCreateTransactionUsecaseWithCorrectValues() throws Exception {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.handle(input);

        verify(usecase).call(input);
    }

    @Test
    void shouldReturn201WithTransactionOnUsecaseSuccess() throws CustomExceptions {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();
        final Transaction expectedTransaction = makeTransaction();
        when(usecase.call(any())).thenReturn(expectedTransaction);

        final ResponseEntity<?> response = sut.handle(input);
        final TransactionPresenter transaction = (TransactionPresenter) response.getBody();

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(transaction);
        assertEquals(expectedTransaction.payerId(), transaction.payerId());
        assertEquals(expectedTransaction.payeeId(), transaction.payeeId());
        assertEquals(expectedTransaction.amount(), transaction.amount());
        assertEquals(expectedTransaction.time(), transaction.time());
    }

    @Test
    void shouldReturn400WithCorrectMessageOnUsecaseCustomException() throws Exception {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();
        final CustomExceptions exception = new CustomExceptions.InsufficientFunds();
        final String exceptionMessage = ErrorPresenter.DICTIONARY.get(exception.getClass());
        mockFailure(exception);

        final ResponseEntity<?> response = sut.handle(input);
        final var responseBody = (ErrorPresenter) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(responseBody);
        assertEquals(exceptionMessage, responseBody.message());
    }
}
