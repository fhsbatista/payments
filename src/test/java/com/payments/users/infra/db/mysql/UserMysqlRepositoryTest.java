package com.payments.users.infra.db.mysql;

import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMysqlRepositoryTest {
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

    @Tag("create user")
    @Test
    void shouldReturnUserOnDbSuccess() {
        final UserMysqlRepository sut = makeSut();
        final CreateUserInput input = makeInput();
        final User result = sut.create(input);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(input.name(), result.name());
        assertEquals(input.email(), result.email());
        assertEquals(input.name(), result.name());
        //Verify hashed pass when it is implemented
    }
}
