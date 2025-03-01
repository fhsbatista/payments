package com.payments.users.domain.entities;

public record User(
        String name,
        String cpf,
        String email
) {
}
