package com.payments.users.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;

public class DbCreateUser implements CreateUser {
    private final CreateUserRepository repository;

    public DbCreateUser(CreateUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User call(CreateUserInput input) {
        return repository.call(input);
    }
}
