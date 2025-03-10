package com.payments.transactions.domain.usecases;

import java.math.BigDecimal;

public record CreateTransactionInput(
        Long payerId,
        Long payeeId,
        BigDecimal amount
) {
}
