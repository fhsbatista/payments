package com.payments.main.validation;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Pattern;

public class EmailValidation implements Validation {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final String fieldName;

    public EmailValidation(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(Object input) throws ValidationException {
        final Optional<String> email = getEmail(input);

        if (email.isEmpty()) {
            throw new ValidationException.InvalidEmail();
        }

        final boolean valid = EMAIL_PATTERN.matcher(email.get()).matches();

        if (!valid) {
            throw new ValidationException.InvalidEmail();
        }
    }

    private Optional<String> getEmail(Object input) {
        try {
            final Field field = input.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return Optional.of(field.get(input).toString());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return Optional.empty();
        }
    }
}