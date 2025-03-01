package com.payments.users.infra.db.mysql;

import com.payments.DatabaseCleaner;
import com.payments.MysqlCleaner;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserMysqlRepositoryTest {
    private final DatabaseCleaner databaseCleaner = new MysqlCleaner();

    UserMysqlRepository makeSut() {
        return new UserMysqlRepository();
    }

    CreateUserInput makeInput() {
        return new CreateUserInput(
                "John Doe",
                "1234567800",
                "john@doe.com",
                "123!@#"
        );
    }

    @BeforeEach
    void setup() {
        databaseCleaner.clean("USERS");
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean("USERS");
    }
    @Tag("create user")
    @Test
    void shouldReturnUserOnDbSuccess() {
        final UserMysqlRepository sut = makeSut();
        final CreateUserInput input = makeInput();

        final Optional<User> result = sut.create(input);

        assertTrue(result.isPresent());
        assertNotNull(result.get().id());
        assertEquals(input.name(), result.get().name());
        assertEquals(input.email(), result.get().email());
        assertEquals(input.name(), result.get().name());
        //Verify hashed pass when it is implemented
    }

    @Tag("create user")
    @Test
    void shouldPersistUserOnDbCorrectly() {
        final UserMysqlRepository sut = makeSut();
        final CreateUserInput input = makeInput();

        sut.create(input);
        final Optional<User> user = sut.getByEmail(input.email());

        assertTrue(user.isPresent());
        assertEquals(input.name(), user.get().name());
        assertEquals(input.cpf(), user.get().cpf());
        assertEquals(input.email(), user.get().email());
    }
}
