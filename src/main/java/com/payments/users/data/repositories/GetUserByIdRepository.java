package com.payments.users.data.repositories;

import com.payments.users.domain.entities.User;

import java.util.Optional;

public interface GetUserByIdRepository {
    Optional<User> getById(Long id);
}
