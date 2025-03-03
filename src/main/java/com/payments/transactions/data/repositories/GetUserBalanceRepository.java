package com.payments.transactions.data.repositories;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetUserBalanceRepository {
    Optional<BigDecimal> getUserBalance(Long userId);
}
