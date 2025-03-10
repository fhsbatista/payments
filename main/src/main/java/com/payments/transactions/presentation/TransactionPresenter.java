package com.payments.transactions.presentation;

import com.payments.transactions.domain.entities.Transaction;
import com.payments.users.domain.entities.User;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionPresenter(
        Long id,
        Long payerId,
        Long payeeId,
        BigDecimal amount,
        Instant time
) {
    public static TransactionPresenter fromTransaction(Transaction transaction) {
        return new TransactionPresenter(
                transaction.id(),
                transaction.payerId(),
                transaction.payeeId(),
                transaction.amount(),
                transaction.time()
        );
    }
}
