package com.payments.users.presentation;

import com.payments.users.domain.entities.User;

public record UserPresenter(
        Long id,
        String name,
        String cpf,
        String email
) {
    public static UserPresenter fromUser(User user) {
        return new UserPresenter(
                user.id(),
                user.name(),
                user.cpf(),
                user.email()
        );
    }
}
