package com.payments.users.infra.db.mysql;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;

public class UserMysqlRepository implements CreateUserRepository {
    @Override
    public User create(CreateUserInput input) {
        return new User(
                123L,
                input.name(),
                input.cpf(),
                input.email()
        );
    }
}
