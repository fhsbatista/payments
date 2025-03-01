package com.payments.users.data.repositories;

import com.payments.users.domain.entities.User;

import java.util.Optional;

public interface GetUserByEmailRepository {
    Optional<User> getByEmail(String email);
}
