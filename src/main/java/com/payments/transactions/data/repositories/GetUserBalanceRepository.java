package com.payments.transactions.data.repositories;

import com.payments.transactions.domain.entities.Transaction;

import java.util.Optional;

public interface GetUserBalanceRepository {
    Optional<Transaction> getUserBalance(Long userId);
}
