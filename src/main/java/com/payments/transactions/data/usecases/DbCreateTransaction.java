package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;

import java.util.Optional;

public class DbCreateTransaction implements CreateTransaction {
    private final CreateTransactionRepository createTransactionRepository;

    public DbCreateTransaction(CreateTransactionRepository createTransactionRepository) {
        this.createTransactionRepository = createTransactionRepository;
    }

    @Override
    public Transaction call(CreateTransactionInput input) throws CustomExceptions {
        final Optional<Transaction> transaction = createTransactionRepository.create(input);

        if (transaction.isEmpty()) {
            throw new CustomExceptions.PersistanceError();
        }

        return transaction.get();
    }
}
