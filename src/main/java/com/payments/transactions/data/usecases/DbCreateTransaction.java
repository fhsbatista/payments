package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;

import java.util.Optional;

public class DbCreateTransaction implements CreateTransaction {
    private final CreateTransactionRepository createTransactionRepository;
    private final GetUserBalanceRepository getUserBalanceRepository;

    public DbCreateTransaction(
            CreateTransactionRepository createTransactionRepository,
            GetUserBalanceRepository getUserBalanceRepository
    ) {
        this.createTransactionRepository = createTransactionRepository;
        this.getUserBalanceRepository = getUserBalanceRepository;
    }

    @Override
    public Transaction call(CreateTransactionInput input) throws CustomExceptions {
        getUserBalanceRepository.getUserBalance(input.payerId());
        final Optional<Transaction> transaction = createTransactionRepository.create(input);

        if (transaction.isEmpty()) {
            throw new CustomExceptions.PersistanceError();
        }

        return transaction.get();
    }
}
