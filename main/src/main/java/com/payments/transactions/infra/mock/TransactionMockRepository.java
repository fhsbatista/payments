package com.payments.transactions.infra.mock;

import com.payments.transactions.data.repositories.GetUserBalanceRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class TransactionMockRepository implements
        GetUserBalanceRepository {


    @Override
    public Optional<BigDecimal> getUserBalance(Long userId) {
        return Optional.of(BigDecimal.valueOf(500000.00));
    }
}
