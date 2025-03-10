package com.payments.transactions.presentation;

import com.payments.main.validation.ValidationException;
import com.payments.transactions.domain.CustomExceptions;

import java.util.Map;

public record ErrorPresenter(String message) {
    public static final Map<Class<? extends Exception>, String> DICTIONARY = Map.of(
            CustomExceptions.PayerNotFound.class, "Payer not found",
            CustomExceptions.PayeeNotFound.class, "Payee not found",
            CustomExceptions.PersistanceError.class, "Could not persist transaction on database. Try again later.",
            CustomExceptions.InsufficientFunds.class, "Payer has not enough balance.",
            CustomExceptions.UnknownBalance.class, "Could not get payer's balance. Try again later.",
            CustomExceptions.NotAuthorized.class, "Transaction has been not authorized."
    );

    public static ErrorPresenter fromException(Exception e) {
        final String message = DICTIONARY.get(e.getClass());

        if (message == null) {
            e.printStackTrace();
            return new ErrorPresenter("Internal server error");
        }

        return new ErrorPresenter(DICTIONARY.get(e.getClass()));
    }

    public static ErrorPresenter fromValidationException(ValidationException e) {
        return new ErrorPresenter(e.toString());
    }
}