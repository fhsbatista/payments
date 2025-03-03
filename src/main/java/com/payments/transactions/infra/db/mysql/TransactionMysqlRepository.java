package com.payments.transactions.infra.db.mysql;

import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import com.payments.util.MysqlUtil;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class TransactionMysqlRepository implements
        CreateTransactionRepository {

    @Override
    public Optional<Transaction> create(CreateTransactionInput input) {
        final String sql = "INSERT INTO " +
                "TRANSACTIONS (payer_id, payee_id, amount, time)" +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = MysqlUtil.conn();
             PreparedStatement statement = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            final Instant instant = Instant.now();
            statement.setLong(1, input.payerId());
            statement.setLong(2, input.payeeId());
            statement.setBigDecimal(3, input.amount());
            statement.setTimestamp(4, Timestamp.from(instant));

            int affectedRows = statement.executeUpdate();
            boolean isPersisted = affectedRows > 0;
            if (isPersisted) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return Optional.of(new Transaction(
                                generatedKeys.getLong(1),
                                input.payerId(),
                                input.payeeId(),
                                input.amount(),
                                instant
                        ));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
