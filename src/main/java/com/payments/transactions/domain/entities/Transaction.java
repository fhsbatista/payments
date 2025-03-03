package com.payments.transactions.domain.entities;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        Long id,
        Long payerId,
        Long payeeId,
        BigDecimal amount,
        Instant time) {
}
