package com.payments.users.domain.usecases;

public record CreateUserInput(
     String name,
     String cpf,
     String email,
     String password
) {}
