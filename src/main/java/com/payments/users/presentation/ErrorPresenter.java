package com.payments.users.presentation;

import com.payments.users.domain.CustomExceptions;

import java.util.Map;

public record ErrorPresenter(String message) {
    public static final Map<Class<? extends CustomExceptions>, String> DICTIONARY = Map.of(
            CustomExceptions.EmailAlreadyRegistered.class, "Email is already registered.",
            CustomExceptions.PersistanceError.class, "Could not persist user on databas. Try again later."
    );

    public static ErrorPresenter fromCustomException(CustomExceptions exception) {
        return new ErrorPresenter(DICTIONARY.get(exception.getClass()));
    }
}