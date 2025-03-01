package com.payments.users.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;

import java.util.Optional;

public class DbCreateUser implements CreateUser {
    private final CreateUserRepository createUserRepository;
    private final GetUserByEmailRepository getUserByEmailRepository;

    public DbCreateUser(
            CreateUserRepository createUserRepository,
            GetUserByEmailRepository getUserByEmailRepository
    ) {
        this.createUserRepository = createUserRepository;
        this.getUserByEmailRepository = getUserByEmailRepository;
    }

    @Override
    public User call(CreateUserInput input) throws CustomExceptions {
        if (isEmailAlreadyRegistered(input.email())) {
            throw new CustomExceptions.EmailAlreadyRegistered();
        }
        return createUserRepository.call(input);
    }

    private boolean isEmailAlreadyRegistered(String email) {
        return getUserByEmailRepository.call(email).isPresent();
    }
}
