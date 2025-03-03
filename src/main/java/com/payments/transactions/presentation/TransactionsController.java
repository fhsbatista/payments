package com.payments.transactions.presentation;

import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;

public class TransactionsController {
    private CreateTransaction usecase;

    public TransactionsController(CreateTransaction usecase) {
        this.usecase = usecase;
    }

    void handle(CreateTransactionInput input) {
        try {
            usecase.call(input);
        } catch (CustomExceptions e) {
            throw new RuntimeException(e);
        }
    }
}
