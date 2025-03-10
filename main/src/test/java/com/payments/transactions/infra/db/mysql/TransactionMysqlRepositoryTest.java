package com.payments.transactions.infra.db.mysql;

import com.payments.DatabaseCleaner;
import com.payments.MysqlCleaner;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import com.payments.users.infra.db.mysql.UserMysqlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionMysqlRepositoryTest {
    private final DatabaseCleaner databaseCleaner = new MysqlCleaner();
    private User payer;
    private User payee;

    TransactionMysqlRepository makeSut() {
        return new TransactionMysqlRepository();
    }

    CreateTransactionInput makeInput() {
        return new CreateTransactionInput(
                payer.id(),
                payee.id(),
                BigDecimal.valueOf(100.0)
        );
    }

    User newUser() {
        final CreateUserInput input = new CreateUserInput(
                "teste",
                "12345678900",
                "email@email.com",
                "123!@#"
        );

        return new UserMysqlRepository().create(input).get();
    }

    @BeforeEach
    void setup() {
        databaseCleaner.clean("TRANSACTIONS");
        payer = newUser();
        payee = newUser();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean("TRANSACTIONS");
    }

    @Tag("create transaction")
    @Test
    void shouldReturnTransactionOnDbSuccess() {
        final TransactionMysqlRepository sut = makeSut();
        final CreateTransactionInput input = makeInput();

        final Optional<Transaction> result = sut.create(input);

        assertTrue(result.isPresent());
        assertNotNull(result.get().id());
        assertNotNull(result.get().time());
        assertEquals(input.payerId(), result.get().payerId());
        assertEquals(input.payeeId(), result.get().payeeId());
        assertEquals(input.amount(), result.get().amount());
    }
}
