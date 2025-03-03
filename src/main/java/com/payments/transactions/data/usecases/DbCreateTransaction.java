package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;

public class DbCreateTransaction implements CreateTransaction {
    private final CreateTransactionRepository createTransactionRepository;

    public DbCreateTransaction(CreateTransactionRepository createTransactionRepository) {
        this.createTransactionRepository = createTransactionRepository;
    }

    @Override
    public Transaction call(CreateTransactionInput input) throws CustomExceptions {
        createTransactionRepository.create(input);
        return null;
    }
}
