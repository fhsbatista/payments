package com.payments.transactions.domain.usecases;

public interface Authorizer {
    boolean isAuthorized(CreateTransactionInput input);
}
