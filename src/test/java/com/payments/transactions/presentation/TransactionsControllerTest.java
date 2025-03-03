package com.payments.transactions.presentation;

import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

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

    @BeforeEach
    void setup() throws CustomExceptions {
        usecase = mock(CreateTransaction.class);
        mockSuccess();
    }

    void mockSuccess() throws CustomExceptions {
        final Transaction transaction = new Transaction(
                1L,
                1L,
                2L,
                BigDecimal.valueOf(100.0),
                Instant.now()
        );
        when(usecase.call(any())).thenReturn(transaction);
    }

    @Test
    void shouldCallCreateTransactionUsecaseWithCorrectValues() throws Exception {
        final TransactionsController sut = makeSut();
        final CreateTransactionInput input = makeInput();

        sut.handle(input);

        verify(usecase).call(input);
    }
}
