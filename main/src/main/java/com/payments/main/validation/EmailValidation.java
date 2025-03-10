package com.payments.main.validation;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Pattern;

public class EmailValidation implements Validation {
    //Regex from emailregex.com
    private static final String EMAIL_REGEX = "" +
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-" +
            "\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
            "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-" +
            "9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-" +
            "\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
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