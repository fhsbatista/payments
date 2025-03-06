package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;

import java.math.BigDecimal;
import java.util.Optional;

public class DbCreateTransaction implements CreateTransaction {
    private final CreateTransactionRepository createTransactionRepository;
    private final GetUserBalanceRepository getUserBalanceRepository;
    private final Authorizer authorizer;

    public DbCreateTransaction(
            CreateTransactionRepository createTransactionRepository,
            GetUserBalanceRepository getUserBalanceRepository,
            Authorizer authorizer
    ) {
        this.createTransactionRepository = createTransactionRepository;
        this.getUserBalanceRepository = getUserBalanceRepository;
        this.authorizer = authorizer;
    }

    @Override
    public Transaction call(CreateTransactionInput input) throws CustomExceptions {
        checkAuthorization(input);
        checkBalance(input);

        return createTransaction(input);
    }

    private void checkAuthorization(CreateTransactionInput input) throws CustomExceptions.NotAuthorized {
        final boolean isAuthorized = authorizer.isAuthorized(input);
        if (!isAuthorized) throw new CustomExceptions.NotAuthorized();
    }

    private void checkBalance(CreateTransactionInput input)
            throws CustomExceptions.InsufficientFunds, CustomExceptions.UnknownBalance {
        final Optional<BigDecimal> balance = getUserBalanceRepository.getUserBalance(input.payerId());

        if (balance.isEmpty()) {
            throw new CustomExceptions.UnknownBalance();
        }

        final boolean hasEnoughBalance = input.amount().compareTo(balance.get()) <= 0;

        if (!hasEnoughBalance) throw new CustomExceptions.InsufficientFunds();
    }

    private Transaction createTransaction(CreateTransactionInput input) throws CustomExceptions.PersistanceError {
        final Optional<Transaction> transaction = createTransactionRepository.create(input);
        if (transaction.isEmpty()) throw new CustomExceptions.PersistanceError();

        return transaction.get();
    }
}
