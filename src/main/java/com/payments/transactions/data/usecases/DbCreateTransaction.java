package com.payments.transactions.data.usecases;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.*;
import com.payments.users.data.repositories.GetUserByIdRepository;
import com.payments.users.domain.entities.User;

import java.math.BigDecimal;
import java.util.Optional;

public class DbCreateTransaction implements CreateTransaction {
    private final Authorizer authorizer;
    private final CreateTransactionRepository createTransactionRepository;
    private final GetUserBalanceRepository getUserBalanceRepository;
    private final SendNotification sendNotification;
    private final GetUserByIdRepository getUserByIdRepository;

    public DbCreateTransaction(
            Authorizer authorizer,
            CreateTransactionRepository createTransactionRepository,
            GetUserBalanceRepository getUserBalanceRepository,
            SendNotification sendNotification,
            GetUserByIdRepository getUserByIdRepository
    ) {
        this.createTransactionRepository = createTransactionRepository;
        this.getUserBalanceRepository = getUserBalanceRepository;
        this.authorizer = authorizer;
        this.sendNotification = sendNotification;
        this.getUserByIdRepository = getUserByIdRepository;
    }

    @Override
    public Transaction call(CreateTransactionInput input) throws CustomExceptions {
        checkAuthorization(input);
        checkBalance(input);

        final Transaction transaction = createTransaction(input);

        final Optional<User> payer = getUserByIdRepository.getById(input.payerId());
        final Optional<User> payee = getUserByIdRepository.getById(input.payeeId());

        if (payer.isEmpty()) throw new CustomExceptions.PayerNotFound();

        final SendNotificationInput payerNotification = new SendNotificationInput(
                payer.get().email(),
                "Your transaction has been cleared"
        );

        final SendNotificationInput payeeNotification = new SendNotificationInput(
                payee.get().email(),
        "You have received a transaction"
        );


        sendNotification.send(payerNotification);
        sendNotification.send(payeeNotification);

        return transaction;
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
