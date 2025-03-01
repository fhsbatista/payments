package com.payments.users.data.repositories;

import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUserInput;

public interface CreateUserRepository {
    User call(CreateUserInput input);
}
