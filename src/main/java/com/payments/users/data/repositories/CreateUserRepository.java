package com.payments.users.data.repositories;

import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;

import java.util.Optional;

public interface CreateUserRepository {
    Optional<User> create(CreateUserInput input);
}
