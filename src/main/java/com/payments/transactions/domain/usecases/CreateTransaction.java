package com.payments.transactions.domain.usecases;

import com.payments.transactions.domain.entities.Transaction;

public interface CreateTransaction {
    Transaction call(CreateTransactionInput input) throws Exception;
}
