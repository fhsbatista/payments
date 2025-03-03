package com.payments.main.validation;

public class EmailValidation implements Validation {
    private final String fieldName;

    public EmailValidation(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(Object input) throws ValidationException {
        throw new ValidationException.InvalidEmail();
    }
}
