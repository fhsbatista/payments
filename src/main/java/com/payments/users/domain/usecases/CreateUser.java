package com.payments.users.domain.usecases;

import com.payments.users.domain.entities.User;

public interface CreateUser {
    User call(CreateUserInput input) throws Exception;
}
