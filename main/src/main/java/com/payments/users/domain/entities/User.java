package com.payments.users.domain.entities;

public record User(
        Long id,
        String name,
        String cpf,
        String email
) {
}
