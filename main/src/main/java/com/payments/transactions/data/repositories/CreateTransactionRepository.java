package com.payments.transactions.data.repositories;

import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import com.payments.users.domain.entities.User;

import java.util.Optional;

public interface CreateTransactionRepository {
    Optional<Transaction> create(CreateTransactionInput input);
}
