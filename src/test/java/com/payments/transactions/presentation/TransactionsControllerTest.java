package com.payments.transactions.presentation;

import com.payments.main.validation.Validation;
import com.payments.main.validation.ValidationException;
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
    private Validation validation;
    private CreateTransaction usecase;

    TransactionsController makeSut() {
        return new TransactionsController(validation, usecase);
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
    void setup() throws Exception {
        validation = mock(Validation.class);
        usecase = mock(CreateTransaction.class);
        mockSuccess();
    }

    void mockSuccess() throws Exception {
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
    void shouldReturn201WithTransactionOnUsecaseSuccess() throws Exception {
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

    @Test
    void shouldReturn500OnUsecaseNotKnownException() throws Exception {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();
        when(usecase.call(input)).thenThrow(new Exception());

        final ResponseEntity<?> response = sut.handle(input);
        final var responseBody = (ErrorPresenter) response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(responseBody);
        assertNotNull("Internal server error", responseBody.message());
    }

    @Test
    void shouldCallValidationWithCorrectValues() throws ValidationException {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.handle(input);

        verify(validation).validate(input);
    }

    @Test
    void shouldReturn400WithCorrectBodyIfValidationThrows() throws ValidationException {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();
        doThrow(new ValidationException.MissingField("amount"))
                .when(validation).validate(input);

        final ResponseEntity<?> response = sut.handle(input);
        final ErrorPresenter body = (ErrorPresenter) response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(body);
        assertEquals("amount is missing", body.message());
    }
}
