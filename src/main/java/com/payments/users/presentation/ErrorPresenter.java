package com.payments.users.presentation;

import com.payments.users.domain.CustomExceptions;

import java.util.Map;

public record ErrorPresenter(String message) {
    public static final Map<Class<? extends Exception>, String> DICTIONARY = Map.of(
            CustomExceptions.EmailAlreadyRegistered.class, "Email is already registered.",
            CustomExceptions.PersistanceError.class, "Could not persist user on databas. Try again later."
    );

    public static ErrorPresenter fromException(Exception exception) {
        final String message = DICTIONARY.get(exception.getClass());

        if (message == null) {
            return new ErrorPresenter("Internal server error");
        }

        return new ErrorPresenter(DICTIONARY.get(exception.getClass()));
    }
}