package com.payments.users.data.usecases;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByCpfRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;

import java.util.Optional;

public class DbCreateUser implements CreateUser {
    private final CreateUserRepository createUserRepository;
    private final GetUserByEmailRepository getUserByEmailRepository;
    private final GetUserByCpfRepository getUserByCpfRepository;

    public DbCreateUser(
            CreateUserRepository createUserRepository,
            GetUserByEmailRepository getUserByEmailRepository,
            GetUserByCpfRepository getUserByCpfRepository
    ) {
        this.createUserRepository = createUserRepository;
        this.getUserByEmailRepository = getUserByEmailRepository;
        this.getUserByCpfRepository = getUserByCpfRepository;
    }

    @Override
    public User call(CreateUserInput input) throws CustomExceptions {
        if (isEmailAlreadyRegistered(input.email())) {
            throw new CustomExceptions.EmailAlreadyRegistered();
        }

        if (isCpfAlreadyRegistered(input.cpf())) {
            throw new CustomExceptions.EmailAlreadyRegistered();
        }

        final Optional<User> result = createUserRepository.create(input);

        if (result.isEmpty()) {
            throw new CustomExceptions.PersistanceError();
        }

        return result.get();
    }

    private boolean isEmailAlreadyRegistered(String email) {
        return getUserByEmailRepository.getByEmail(email).isPresent();
    }

    private boolean isCpfAlreadyRegistered(String cpf) {
        return getUserByCpfRepository.getByCpf(cpf).isPresent();
    }
}
