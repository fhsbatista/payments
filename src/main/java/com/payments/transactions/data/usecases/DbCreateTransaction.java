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
        authorizer.isAuthorized(input);
        if (!isBalanceValid(input)) throw new CustomExceptions.InsufficientFunds();
        final Optional<Transaction> transaction = createTransactionRepository.create(input);
        if (transaction.isEmpty()) throw new CustomExceptions.PersistanceError();

        return transaction.get();
    }

    private boolean isBalanceValid(CreateTransactionInput input) throws CustomExceptions.UnknownBalance {
        final Optional<BigDecimal> balance = getUserBalanceRepository
                .getUserBalance(input.payerId());

        if (balance.isEmpty()) {
            throw new CustomExceptions.UnknownBalance();
        }

        return input.amount().compareTo(balance.get()) <= 0;
    }
}
